package com.admin.sys.mapper;

import com.admin.sys.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


public interface UserMapper extends BaseMapper<User> {
    List<String> getRoleNameByUserId(Integer userId);
}
