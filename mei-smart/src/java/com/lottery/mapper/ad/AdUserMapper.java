package com.lottery.mapper.ad;



import java.util.List;

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
}