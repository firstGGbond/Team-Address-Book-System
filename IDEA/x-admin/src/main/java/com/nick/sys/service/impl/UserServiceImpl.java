package com.nick.sys.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nick.common.utils.JwtUtils;
import com.nick.sys.entity.RoleMenu;
import com.nick.sys.entity.User;
import com.nick.sys.mapper.UserMapper;
import com.nick.sys.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author nick
 * @since 2024-10-25
 */
@Service
public class  UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Map<String, Object> login(User user) {
        // 根据用户名和密码查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        wrapper.eq(User::getPassword, user.getPassword());
        User loginUser =  this.baseMapper.selectOne(wrapper);
        // 结果不为空， 则生成token, 并将用户信息存入redis
        if (loginUser != null) {
            // 暂时用UUID， 终极方案为jwt
            // String key = "user: " + UUID.randomUUID();

            //存入redis
            loginUser.setPassword(null);
            //redisTemplate.opsForValue().set(key,loginUser,30, TimeUnit.MINUTES);

            //创建jwt
            String  token = jwtUtils.createToken(loginUser);
            //返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token );
            return data;
        }


        return null;
    }

    @Override
    public Map<String, Object> getUserInfo(String token) {
        //根据token获取用户信息，redis
        //Object obj = redisTemplate.opsForValue().get(token);
        User user = null;
        try {
             user = jwtUtils.parseToken(token, User.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
        }
        // user = JSON.parseObject(JSON.toJSONString(obj),User.class);
        if(user != null){
            Map<String, Object> data = new HashMap<>();
            data.put("name",user.getUsername());
            data.put("avatar",user.getAvatar());

            //角色
            List<String> roleList = this.getBaseMapper().getRoleNamesByUserId(user.getId());
            data.put("roles", roleList);
            return data;
        }
        return null;
    }

    @Override
    public void logout(String token) {
        //redisTemplate.delete(token);
    }


}
