package com.lottery.mapper.sys;

import java.util.List;

import com.lottery.model.sys.SysMenu;
import com.lottery.model.sys.SysRole;

public interface SysRoleMapper {
    int insert(SysRole record);

    int insertSelective(SysRole record);
    
    SysRole findByRoleId(Integer roleId);
}