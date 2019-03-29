package com.lottery.mapper.ad;

import com.lottery.model.ad.AdBiding;

public interface AdBidingMapper {
    int deleteByPrimaryKey(Integer biding_id);

    int insert(AdBiding record);

    int insertSelective(AdBiding record);

    AdBiding selectByPrimaryKey(Integer biding_id);

    int updateByPrimaryKeySelective(AdBiding record);

    int updateByPrimaryKey(AdBiding record);
}