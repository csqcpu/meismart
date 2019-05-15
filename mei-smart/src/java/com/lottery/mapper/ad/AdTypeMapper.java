package com.lottery.mapper.ad;

import com.lottery.model.ad.AdType;

public interface AdTypeMapper {
    int deleteByPrimaryKey(Integer type_id);

    int insert(AdType record);

    int insertSelective(AdType record);

    AdType selectByPrimaryKey(Integer type_id);

    int updateByPrimaryKeySelective(AdType record);

    int updateByPrimaryKey(AdType record);
}