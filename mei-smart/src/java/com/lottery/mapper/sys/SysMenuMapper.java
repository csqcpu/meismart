package com.lottery.mapper.sys;

import java.util.List;
import java.util.Map;

import com.lottery.model.sys.SysMenu;

public interface SysMenuMapper {
    int deleteByPrimaryKey(Integer menu_id);

    int insert(SysMenu record);

    int insertSelective(SysMenu record);

    SysMenu selectByPrimaryKey(Integer menu_id);

    int updateByPrimaryKeySelective(SysMenu record);

    int updateByPrimaryKey(SysMenu record);
    
    List<SysMenu> findByMenuIds(Map<String,Object> param);
}