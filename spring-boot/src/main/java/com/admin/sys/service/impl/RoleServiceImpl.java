package com.admin.sys.service.impl;

import com.admin.sys.entity.Role;
import com.admin.sys.entity.RoleMenu;
import com.admin.sys.mapper.RoleMapper;
import com.admin.sys.mapper.RoleMenuMapper;
import com.admin.sys.service.IRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;


    @Override
    @Transactional(rollbackFor=Exception.class)
    public void addRole(Role role) {
        roleMapper.insert(role);
        setRoleMenuByRoleId(role);
    }

    private void setRoleMenuByRoleId(Role role) {
        if (role.getMenuIdList() != null) {
            for (Integer menuId : role.getMenuIdList()) {
                roleMenuMapper.insert(new RoleMenu(null, role.getRoleId(), menuId));
            }
        }
    }

    @Override
    public Role getRoleById(Integer roleId) {
        Role role = roleMapper.selectById(roleId);
        List<Integer> menuIdList = roleMenuMapper.getMenuIdListByRoleId(roleId);
        role.setMenuIdList(menuIdList);
        return role;
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void updateRole(Role role) {
        // 修改角色表
        roleMapper.updateById(role);
        // 删除原有所有权限
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, role.getRoleId());
        roleMenuMapper.delete(wrapper);
        // 重新设置权限
        setRoleMenuByRoleId(role);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void deleteRoleById(Integer roleId) {
        // 删除角色
        roleMapper.deleteById(roleId);
        // 清除角色权限
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, roleId);
        roleMenuMapper.delete(wrapper);
    }
}
