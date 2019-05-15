package com.lottery.mapper.ad;

import com.lottery.model.ad.AdPublish;

public interface AdPublishMapper {
    int deleteByPrimaryKey(Integer publish_id);

    int insert(AdPublish record);

    int insertSelective(AdPublish record);

    AdPublish selectByPrimaryKey(Integer publish_id);

    int updateByPrimaryKeySelective(AdPublish record);

    int updateByPrimaryKey(AdPublish record);
}