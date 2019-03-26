package com.lottery.service.ad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.lottery.mapper.ad.AdOnlineMapper;

@Service
public class AdFeeService200Impl implements AdFeeService {
	@Autowired
	AdOnlineMapper adOnlineMapper;

	@Override
	public int getFeeIdValue(Integer id) {
		//AdOnline adOnline = adOnlineDao.findById(id);
		return 200;
	}
}