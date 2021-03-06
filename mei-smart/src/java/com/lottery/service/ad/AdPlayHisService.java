package com.lottery.service.ad;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lottery.mapper.ad.AdOnlineMapper;
import com.lottery.mapper.ad.AdPlayHisMapper;
import com.lottery.model.ad.AdOnline;
import com.lottery.model.ad.AdPlayHis;

@Service
public class AdPlayHisService{
	@Autowired
	AdPlayHisMapper adPlayHisMapper;
	public int insert(AdPlayHis adPlayHis){
		return adPlayHisMapper.insert(adPlayHis);
	}
	public int insertByBatch(List<AdPlayHis> adPlayHisList){
		return adPlayHisMapper.insertByBatch(adPlayHisList);
	}

}