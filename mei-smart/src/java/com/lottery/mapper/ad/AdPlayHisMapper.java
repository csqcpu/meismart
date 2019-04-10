package com.lottery.mapper.ad;

import java.util.List;

import com.lottery.model.ad.AdPlayHis;

public interface AdPlayHisMapper {
    int deleteByPrimaryKey(Integer playhis_id);

    int insert(AdPlayHis record);

    int insertSelective(AdPlayHis record);

    AdPlayHis selectByPrimaryKey(Integer playhis_id);

    int updateByPrimaryKeySelective(AdPlayHis record);

    int updateByPrimaryKey(AdPlayHis record);
    
    int insertByBatch(List<AdPlayHis> recordList);
}