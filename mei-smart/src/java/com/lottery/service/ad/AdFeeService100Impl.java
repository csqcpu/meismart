package com.lottery.service.ad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.lottery.mapper.ad.AdOnlineMapper;
import com.lottery.model.ad.AdUser;
import com.lottery.redis.MybatisRedisCache;

@Service
public class AdFeeService100Impl implements AdFeeService {
	@Autowired
	AdOnlineMapper adOnlineMapper;
	MybatisRedisCache mybatisRedisCache = new MybatisRedisCache(AdFeeService100Impl.class.toString());
	@Override
	public int getFeeIdValue(Integer id) {
		String key = "meismart:ad_user:user_id:1";
		AdUser adUser = (AdUser)mybatisRedisCache.getObject(key);
		int account= adUser.getAccount();
		adUser.setAccount(account-1);
		mybatisRedisCache.putObject(key, adUser);
		return 1;
	}
}