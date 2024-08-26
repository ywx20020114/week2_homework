package com.admin.sys.service;

import com.admin.sys.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface IMenuService extends IService<Menu> {

    List<Menu> getAllMenu();

    List<Menu> getMenuListByUserId(Integer userId);
}
