package com.lottery.mapper.ad;

import com.lottery.model.ad.AdContent;

public interface AdContentMapper {
    int deleteByPrimaryKey(Integer content_id);

    int insert(AdContent record);

    int insertSelective(AdContent record);

    AdContent selectByPrimaryKey(Integer content_id);

    int updateByPrimaryKeySelective(AdContent record);

    int updateByPrimaryKey(AdContent record);
}