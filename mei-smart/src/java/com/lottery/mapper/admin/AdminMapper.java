package com.lottery.mapper.admin;



import java.util.List;
import java.util.Map;

import com.lottery.model.admin.Admin;

public interface AdminMapper {
    int deleteByPrimaryKey(String username);

    int insert(Admin record);

    int insertSelective(Admin record);

    Admin selectByPrimaryKey(String username);

    int updateByPrimaryKeySelective(Admin record);

    int updateByPrimaryKey(Admin record);
    
    List<Admin> findAll();
    
    int updateByAdminId(Admin Admin);
    
    Admin findByUserName(String username);
    
    int delByAdminName(String username);
 
    int update(Admin record);
    
    List<Admin> findByParam(Map<String,Object> param);
}