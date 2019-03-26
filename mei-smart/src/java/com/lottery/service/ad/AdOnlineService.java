package com.lottery.service.ad;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lottery.mapper.ad.AdOnlineMapper;
import com.lottery.model.ad.AdOnline;

@Service
public class AdOnlineService{
	@Autowired
	AdOnlineMapper adOnlineMapper;
	public AdOnline findByOnlineId(Integer user_id){
		return adOnlineMapper.findByOnlineId(user_id);
	}

}