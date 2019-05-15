package com.lottery.service.sys;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.tomcat.util.http.mapper.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lottery.mapper.ad.AdUserMapper;
import com.lottery.mapper.sys.SysMenuMapper;
import com.lottery.mapper.sys.SysRoleMapper;
import com.lottery.mapper.sys.SysRolePermMapper;
import com.lottery.model.ad.AdUser;
import com.lottery.model.sys.SysMenu;
import com.lottery.model.sys.SysRole;
import com.lottery.model.sys.SysRolePerm;

 
@Service
@Transactional
public class SysRolePermService{
	@Resource
	public SysRolePermMapper sysRolePermMapper;

    public SysRolePerm findByRoleId(Integer roleId,String actionPath){
    	Map<String,Object> param = new HashMap<String,Object>();
    	param.put("path", actionPath);
    	param.put("roleId", roleId);
    	return sysRolePermMapper.findByRoleId(param);
    }
 
}