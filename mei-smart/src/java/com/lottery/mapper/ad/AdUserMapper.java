package com.lottery.mapper.ad;



import java.util.List;
import java.util.Map;

import com.lottery.model.ad.AdUser;

public interface AdUserMapper {
    int deleteByPrimaryKey(String username);

    int insert(AdUser record);

    int insertSelective(AdUser record);

    AdUser selectByPrimaryKey(String username);

    int updateByPrimaryKeySelective(AdUser record);

    int updateByPrimaryKey(AdUser record);
    
    List<AdUser> findAll();
    
    int updateByUserId(AdUser adUser);
    
    AdUser findByUserName(String username);
    
    int delByUserName(String username);
 
    int update(AdUser record);
    
    List<AdUser> findByParam(Map<String,Object> param);
    
    int deleteBatch(List<AdUser> adUserList);
    
    int submitCheck(List<AdUser> adUserList);
    
    int checkPass(List<AdUser> adUserList);
    
    int checkFail(List<AdUser> adUserList);
}