package com.lottery.mapper.sys;

import java.util.Map;

import com.lottery.model.sys.SysRolePerm;

public interface SysRolePermMapper {
    int insert(SysRolePerm record);

    int insertSelective(SysRolePerm record);
    
    SysRolePerm findByRoleId(Map<String,Object> param);
}