package com.lottery.controller.ad;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.util.RequestUtil;
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
import com.lottery.service.ad.AdFeeService;
import com.lottery.service.ad.AdOnlineService;
import com.lottery.service.ad.AdPlayHisService;
import com.lottery.util.RequestUtils;

@Controller
public class AdCotroller implements ApplicationContextAware {
	@Autowired
	private AdOnlineService adOnlineService;
	@Autowired
	private AdPlayHisService adPlayHisService;
	ApplicationContext applicationContext;



	@ResponseBody
	@RequestMapping(value = "/ad/getad")
	public void GetAd(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject requestJson = RequestUtils.getRequestJsonObject(request);
		Integer adId = requestJson.getInteger("adid");
		// Integer adId = Integer.valueOf(request.getParameter("id"));
		AdOnline adOnline = adOnlineService.findByOnlineId(adId);
		if (adOnline == null) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", -1);
			jsonObject.put("adid", adId);
			jsonObject.put("msg", "无该id广告");
			response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
			response.setContentType("text/json; charset=UTF-8");
			return;
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", 200);
		jsonObject.put("adid", adId);
		jsonObject.put("link", adOnline.getUrl());
		response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");
	}
	
	@ResponseBody
	@RequestMapping(value = "/ad/addadhis")
	public void AddAdHis(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject requestJson = RequestUtils.getRequestJsonObject(request);
		Integer online_id = requestJson.getInteger("online_id");
		//Timestamp tstartdt =requestJson.getTimestamp("startdt");
		Date startdt =requestJson.getTimestamp("startdt");
		Date enddt =requestJson.getTimestamp("enddt");
		Integer time = requestJson.getInteger("time");
		AdPlayHis adPlayHis= new AdPlayHis();
		adPlayHis.setOnline_id(online_id);
		adPlayHis.setStartdt(startdt);
		adPlayHis.setEnddt(enddt);
		adPlayHis.setPlaytime(time);
		int insertnum= adPlayHisService.insert(adPlayHis);
		if (insertnum == 0) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", -1);
			jsonObject.put("adid", online_id);
			jsonObject.put("msg", "无该id广告");
			response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
			response.setContentType("text/json; charset=UTF-8");
			return;
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", 200);
		jsonObject.put("online_id", online_id);
		response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");
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
		Cookie cookie = new Cookie("mycookie","sweet");
		response.addCookie(cookie);
		HttpSession session = request.getSession();
		session.setAttribute("iflogin", true);
		session.setAttribute("iflogim", "1234569");
	}
	
	@ResponseBody
	@RequestMapping(value = "/get")
	public void ListSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		String seesionStr=session.getAttribute("iflogin").toString();
		seesionStr +=session.getAttribute("iflogim").toString();
		response.getOutputStream().write(seesionStr.getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}