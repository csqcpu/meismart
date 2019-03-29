package com.lottery.mapper.ad;

import com.lottery.model.ad.AdOnline;

public interface AdOnlineMapper {
    int deleteByPrimaryKey(Integer online_id);

    int insert(AdOnline record);

    int insertSelective(AdOnline record);

    AdOnline selectByPrimaryKey(Integer online_id);

    int updateByPrimaryKeySelective(AdOnline record);

    int updateByPrimaryKey(AdOnline record);
    
    AdOnline  findByOnlineId(Integer onlineId);
    
    AdOnline  findByLocationId(Integer locationId);
}