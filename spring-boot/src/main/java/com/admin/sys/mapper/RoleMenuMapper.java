package com.admin.sys.mapper;

import com.admin.sys.entity.RoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;


public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    public List<Integer> getMenuIdListByRoleId(Integer roleId);
}
