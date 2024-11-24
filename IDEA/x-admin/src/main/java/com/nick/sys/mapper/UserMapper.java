package com.nick.sys.mapper;

import com.nick.sys.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author nick
 * @since 2024-10-25
 */
public interface UserMapper extends BaseMapper<User> {

    public List<String> getRoleNamesByUserId(Integer id);
}
