package com.lottery.service.sys;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.tomcat.util.http.mapper.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lottery.mapper.ad.AdUserMapper;
import com.lottery.mapper.sys.SysMenuMapper;
import com.lottery.mapper.sys.SysRoleMapper;
import com.lottery.model.ad.AdUser;
import com.lottery.model.sys.SysMenu;
import com.lottery.model.sys.SysRole;

 
@Service
@Transactional
public class SysRoleService{
	@Resource
	public SysRoleMapper sysRoleMapper;

    public SysRole findByRoleId(Integer roleId){
    	return sysRoleMapper.findByRoleId(roleId);
    }
 
}