package com.lottery.controller.ad;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.util.RequestUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONObject;
import com.lottery.model.ad.AdOnline;
import com.lottery.model.ad.AdPlayHis;
import com.lottery.model.ad.AdUser;
import com.lottery.redis.MybatisRedisCache;
import com.lottery.schedule.ad.InsertAdPlayHisschedule;
import com.lottery.service.ad.AdFeeService;
import com.lottery.service.ad.AdOnlineService;
import com.lottery.service.ad.AdPlayHisService;
import com.lottery.util.RequestUtils;

import redis.clients.jedis.Transaction;

@Controller
public class AdCotroller implements ApplicationContextAware {
	private static Logger logger = Logger.getLogger(AdCotroller.class);
	ExecutorService executor = Executors.newFixedThreadPool(1); // 1个线程池并发数
	@Autowired
	private AdOnlineService adOnlineService;
	@Autowired
	private AdPlayHisService adPlayHisService;
	@Autowired
	MybatisRedisCache mybatisRedisCache;
	ApplicationContext applicationContext;
	Semaphore feeSemaphore = new Semaphore(1, true);
	
	@ResponseBody
	@RequestMapping(value = "/rest/ad/userlogin")
	public void userlogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject requestJson = RequestUtils.getRequestJsonObject(request);
		Integer locationid = requestJson.getInteger("locationid");
		AdOnline adOnline = adOnlineService.findByOnlineId(locationid);
		if (adOnline == null) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", -1);
			jsonObject.put("msg", "locationid=" + locationid + " not found");
			response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
			response.setContentType("text/json; charset=UTF-8");
			return;
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", 200);
		jsonObject.put("onlineid", adOnline.getOnline_id());
		jsonObject.put("url", adOnline.getUrl());
		response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");
	}

	@ResponseBody
	@RequestMapping(value = "/rest/ad/getvideoad")
	public void GetAd(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject requestJson = RequestUtils.getRequestJsonObject(request);
		Integer locationid = requestJson.getInteger("locationid");
		AdOnline adOnline = adOnlineService.findByOnlineId(locationid);
		if (adOnline == null) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", -1);
			jsonObject.put("msg", "locationid=" + locationid + " not found");
			response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
			response.setContentType("text/json; charset=UTF-8");
			return;
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", 200);
		jsonObject.put("onlineid", adOnline.getOnline_id());
		jsonObject.put("url", adOnline.getUrl());
		response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");
	}

	@ResponseBody
	@RequestMapping(value = "/rest/ad/adhis")
	public void AddAdHis(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject requestJson = RequestUtils.getRequestJsonObject(request);
		Integer onlineId = requestJson.getInteger("onlineid");
		// Timestamp tstartdt =requestJson.getTimestamp("startdt");
		Date startdt = requestJson.getTimestamp("startdt");
		Date enddt = requestJson.getTimestamp("enddt");
		Integer duration = requestJson.getInteger("duration");
		AdPlayHis adPlayHis = new AdPlayHis();
		adPlayHis.setOnline_id(onlineId);
		adPlayHis.setStartdt(startdt);
		adPlayHis.setEnddt(enddt);
		adPlayHis.setDuration(duration);
		// int insertnum= adPlayHisService.insert(adPlayHis);
		final String ADPLAYHIS_MQ = "adplayhis_mq";
		long insertnum = mybatisRedisCache.mqSend(ADPLAYHIS_MQ, adPlayHis);

		if (insertnum == 0) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", -1);
			jsonObject.put("msg", "onlineid=" + onlineId + " not found");
			response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
			response.setContentType("text/json; charset=UTF-8");
			return;
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", 200);
		response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");

		AdOnline adOnline = adOnlineService.findByOnlineId(onlineId);
		String userName = adOnline.getUsername();
		int price = 1;
		String watchkey = "aduser:username:" + userName;
		try {
			feeSemaphore.acquire();
			AdUser adUser = (AdUser) mybatisRedisCache.getObject(watchkey);
			int account = adUser.getAccount();
			if (account >= price) {
				account = account - price;
				adUser.setAccount(account);
				mybatisRedisCache.putObject(watchkey,adUser);
			} else {
				logger.info("用户：" + userName + "费用不足");
				return;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			feeSemaphore.release();
		}

	}

	public class FeeRunable implements Runnable {
		MybatisRedisCache mybatisRedisCache;
		String userName;
		Integer price;

		public FeeRunable(MybatisRedisCache mybatisRedisCache, String userName, Integer price) {
			this.mybatisRedisCache = mybatisRedisCache;
			this.userName = userName;
			this.price = price;
		}

		@Override
		public void run() {
			String key = "aduser:username:" + userName;

		}

	}

	@ResponseBody
	@RequestMapping(value = "/test")
	public void test(HttpServletRequest request, HttpServletResponse response) throws IOException {

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", -1);
		jsonObject.put("adid", 1);
		jsonObject.put("msg", "无该id广告");
		response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");
	}

	@ResponseBody
	@RequestMapping(value = "/set")
	public void GetSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Cookie cookie = new Cookie("mycookie", "sweet");
		response.addCookie(cookie);
		HttpSession session = request.getSession();
		session.setAttribute("iflogin", true);
		session.setAttribute("iflogim", "1234569");
	}

	@ResponseBody
	@RequestMapping(value = "/get")
	public void ListSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		String seesionStr = session.getAttribute("iflogin").toString();
		seesionStr += session.getAttribute("iflogim").toString();
		response.getOutputStream().write(seesionStr.getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}