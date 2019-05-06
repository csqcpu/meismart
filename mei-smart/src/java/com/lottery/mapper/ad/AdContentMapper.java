package com.lottery.mapper.ad;

import java.util.List;
import java.util.Map;

import com.lottery.model.ad.AdContent;
import com.lottery.model.ad.AdUser;

public interface AdContentMapper {
    int deleteByPrimaryKey(Integer content_id);

    int insert(AdContent record);

    int insertSelective(AdContent record);

    AdContent selectByPrimaryKey(Integer content_id);

    int updateByPrimaryKeySelective(AdContent record);

    int updateByPrimaryKey(AdContent record);
    
    List<AdContent> findByParam(Map<String,Object> param);
    
    int update(AdContent record);
    int delByContentId(Integer content_id);
}