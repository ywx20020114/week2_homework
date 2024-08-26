package com.admin.sys.mapper;

import com.admin.sys.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface MenuMapper extends BaseMapper<Menu> {
    List<Menu> getMenuListByUserId(@Param("userId") Integer userId, @Param("pid") Integer pid);

}
