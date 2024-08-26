package com.admin.sys.service;

import com.admin.sys.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IRoleService extends IService<Role> {

    void addRole(Role role);

    Role getRoleById(Integer roleId);

    void updateRole(Role role);

    void deleteRoleById(Integer roleId);
}
