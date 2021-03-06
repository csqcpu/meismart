package com.lottery.schedule.ad;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.lottery.model.ad.AdOnline;
import com.lottery.model.ad.AdPlayHis;
import com.lottery.model.ad.AdUser;
import com.lottery.redis.MybatisRedisCache;
import com.lottery.service.ad.AdOnlineService;
import com.lottery.service.ad.AdPlayHisService;
import com.lottery.service.ad.AdUserService;
import com.lottery.util.Object2Map;

@Service
public class InsertAdPlayHisschedule implements ApplicationListener<ContextRefreshedEvent> {
	private static Logger logger = Logger.getLogger(InsertAdPlayHisschedule.class);
	ExecutorService executor = Executors.newFixedThreadPool(1); // 20个线程池并发数
	@Autowired
	AdPlayHisService adPlayHisService;
	@Autowired
	MybatisRedisCache mybatisRedisCache;
	// MybatisRedisCache mybatisRedisCache = new
	// MybatisRedisCache(InsertAdPlayHisschedule.class.toString());
	final static String ADPLAYHIS_MQ = "adplayhis_mq";

	public void doJob() {
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					processAdPlayHisMQ();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void processAdPlayHisMQ() throws InterruptedException {
		List<AdPlayHis> adPlayHisList = new ArrayList<AdPlayHis>();
		AdPlayHis adPlayHis = (AdPlayHis) mybatisRedisCache.mqReceiverd(ADPLAYHIS_MQ);
		Integer insCount = 0;
		//测试插表时间比较
		long startdt = System.currentTimeMillis();
		while (adPlayHis != null) {
			adPlayHisService.insert(adPlayHis);
			adPlayHisList.add(adPlayHis);
			insCount++;
			//Thread.sleep(10000);
			adPlayHis = (AdPlayHis) mybatisRedisCache.mqReceiverd(ADPLAYHIS_MQ);
		}
		long enddt = System.currentTimeMillis();
		logger.info("处理广告MQ条数：" + insCount);
		logger.info("逐条插表时间："+(enddt-startdt)+"ms");
		startdt = System.currentTimeMillis();
		if (adPlayHisList.isEmpty() == false) {
			adPlayHisService.insertByBatch(adPlayHisList);
		}
		enddt = System.currentTimeMillis();
		logger.info("批量插表时间："+(enddt-startdt)+"ms");
		
	}

	// 加载秒杀数据，这里主要加载改动频繁的数据,定时更新
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		doJob();
	}

}
