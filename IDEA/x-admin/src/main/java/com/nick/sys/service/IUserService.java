package com.nick.sys.service;

import com.nick.sys.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nick.sys.mapper.UserMapper;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author nick
 * @since 2024-10-25
 */
public interface IUserService extends IService<User> {

    Map<String, Object> login(User user);

    Map<String, Object> getUserInfo(String token);

    void logout(String token);
}
