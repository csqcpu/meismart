package com.lottery.schedule.ad;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.lottery.model.ad.AdOnline;
import com.lottery.model.ad.AdUser;
import com.lottery.redis.MybatisRedisCache;
import com.lottery.service.ad.AdOnlineService;
import com.lottery.service.ad.AdUserService;
import com.lottery.util.Object2Map;

@Service
public class SynAdUserSchedule implements ApplicationListener<ContextRefreshedEvent> {
	private static Logger logger = Logger.getLogger(SynAdUserSchedule.class);
	@Autowired
	AdUserService adUserService;
	MybatisRedisCache mybatisRedisCache = new MybatisRedisCache(SynAdUserSchedule.class.toString());
	final static String ADUSER_KEYS = "aduser_keys";

	// 加载数据到redis
	public void getDataFromDB2Redis() {
		List<String> keys = new ArrayList<String>();
		List<AdUser> adUserList = adUserService.findAll();
		if (adUserList == null) {
			return;
		}
		for (AdUser adUser : adUserList) {
			String key = "aduser:username:" + adUser.getUsername();
			if (mybatisRedisCache.getObject(key) == null) {
				mybatisRedisCache.putObject(key, adUser);
				logger.info("成功加载数据" + key + "->" + adUser.toString());
			}
			keys.add(key);
		}
		mybatisRedisCache.putObject(ADUSER_KEYS, keys);
	}

	// 同步数据到数据库
	public void updatetDataFromRedis2DB() throws Exception {
		List<String> keys = (List<String>) mybatisRedisCache.getObject(ADUSER_KEYS);
		if (keys == null || keys.isEmpty()) {
			logger.info("广告用户缓存数据为null,不需要更新！");
			return;
		}
		for (String key : keys) {
			AdUser adUser;
			if (mybatisRedisCache.getObject(key) != null) {
				adUser = (AdUser) mybatisRedisCache.getObject(key);
				Integer successCount = adUserService.updateByUserId(adUser);
				if (successCount.equals(1))
					logger.info("成功更行数据" + key + "->" + adUser.toString());
			}
		}
	}

	// 加载秒杀数据，这里主要加载改动频繁的数据,定时更新
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		getDataFromDB2Redis();
	}

}
