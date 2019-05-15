package com.lottery.mapper.ad;

import com.lottery.model.ad.AdLocation;

public interface AdLocationMapper {
    int deleteByPrimaryKey(Integer location_id);

    int insert(AdLocation record);

    int insertSelective(AdLocation record);

    AdLocation selectByPrimaryKey(Integer location_id);

    int updateByPrimaryKeySelective(AdLocation record);

    int updateByPrimaryKey(AdLocation record);
}