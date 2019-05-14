package com.lottery.controller.ad;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.util.RequestUtil;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.mapper.Mapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lottery.model.ad.AdOnline;
import com.lottery.model.ad.AdPlayHis;
import com.lottery.model.ad.AdUser;
import com.lottery.model.auth.Perm;
import com.lottery.model.auth.PureUser;
import com.lottery.redis.MybatisRedisCache;
import com.lottery.schedule.ad.InsertAdPlayHisschedule;
import com.lottery.service.ad.AdFeeService;
import com.lottery.service.ad.AdOnlineService;
import com.lottery.service.ad.AdPlayHisService;
import com.lottery.service.ad.AdUserService;
import com.lottery.service.auth.Auth;
import com.lottery.util.AES;
import com.lottery.util.APIResponseUtil;
import com.lottery.util.RequestUtils;
import com.lottery.util.TOKEN;

import redis.clients.jedis.Transaction;

@Controller
public class AdUserController implements ApplicationContextAware {
	private static Logger logger = Logger.getLogger(AdUserController.class);
	ExecutorService executor = Executors.newFixedThreadPool(1); // 1个线程池并发数
	@Autowired
	private AdOnlineService adOnlineService;
	@Autowired
	private AdUserService adUserService;
	@Autowired
	private Auth auth;
	@Autowired
	MybatisRedisCache mybatisRedisCache;
	ApplicationContext applicationContext;
	Semaphore feeSemaphore = new Semaphore(1, true);

	@ResponseBody
	@RequestMapping(value = "/rest/ad/userlogin")
	public void userlogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			JSONObject requestJson = RequestUtils.getRequestJsonObject(request);
			String username = requestJson.getString("username");
			String password = requestJson.getString("password");
			AdUser adUser = adUserService.findByUserName(username);
			if (adUser.getPassword() == null) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("code", -1);
				jsonObject.put("msg", "username=" + username + " not found");
				response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
				response.setContentType("text/json; charset=UTF-8");
				return;
			}
			password = AES.aesDecrypt(password, AES.complementKey(adUser.getPassword(), 16));
			String token = TOKEN.makeToken();
			long timestamp = System.currentTimeMillis();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", 0);
			jsonObject.put("msg", "登入成功");
			jsonObject.put("token", token);
			jsonObject.put("timestamp", timestamp);
			response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
			response.setContentType("text/json; charset=UTF-8");
			// 用户登录信息存放在redis
			PureUser pureUser = JSONObject.toJavaObject((JSONObject) JSONObject.toJSON(adUser), PureUser.class);
			pureUser.setTimeStmap(timestamp);
			auth.setUserInfoByToken(token, pureUser);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@ResponseBody
	@RequestMapping(value = "/rest/ad/adduser")
	public void adduser(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException, IOException {
		JSONObject requestJson;
		JSONObject jsonObject = new JSONObject();
		try {
			String path = request.getRequestURI();
			requestJson = RequestUtils.getRequestJsonObject(request);
			String token = requestJson.getString("token");
			PureUser pureUser = auth.authenticateUser(token);
			String permStr = auth.getUserPermissionByAction(token, path, Auth.INSERT_ACTION);
			String dataStr = AES.aesDecrypt(requestJson.getString("data"),
					AES.complementKey(pureUser.getPassword(), 16));
			JSONObject dataOject = (JSONObject) JSONObject.parse(dataStr);
			JSONArray dataArray = dataOject.getJSONArray("data");
			auth.authenticateTimeStamp(token, dataOject.getLong("timestamp").longValue());

			JSONObject jsonRecord = dataArray.getJSONObject(0);
			AdUser adUser = JSONObject.toJavaObject(jsonRecord, AdUser.class);
			adUser.setStatus(0);
			adUser.setCreateuser(pureUser.getUsername());
			adUser.setCreatedt(new Date());
			int insertCount = adUserService.insert(adUser);
			long timestamp = System.currentTimeMillis();
			jsonObject.put("code", 0);
			jsonObject.put("msg", "成功");
			jsonObject.put("count", insertCount);
			jsonObject.put("timestamp", timestamp);
			jsonObject.put("data", new ArrayList());
			JSONArray outdataArray = jsonObject.getJSONArray("data");
			String outdataStr = JSON.toJSONString(outdataArray);
			String DataEncrypt = AES.aesEncrypt(outdataStr, AES.complementKey(pureUser.getPassword(), 16));
			jsonObject.put("data", DataEncrypt);
			
			pureUser.setTimeStmap(timestamp);
			auth.setUserInfoByToken(token, pureUser);
		} catch (Exception e) {
			jsonObject = APIResponseUtil.makeErrorJSON(e);
		}
		response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");
	}

	@ResponseBody
	@RequestMapping(value = "/rest/ad/updateuser")
	public void updateuser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject requestJson;
		JSONObject jsonObject = new JSONObject();
		try {
			String path = request.getRequestURI();
			requestJson = RequestUtils.getRequestJsonObject(request);
			String token = requestJson.getString("token");
			PureUser pureUser = auth.authenticateUser(token);
			String permStr = auth.getUserPermissionByAction(token, path, Auth.UPDATE_ACTION);
			String dataStr = AES.aesDecrypt(requestJson.getString("data"),
					AES.complementKey(pureUser.getPassword(), 16));
			JSONObject dataOject = (JSONObject) JSONObject.parse(dataStr);
			JSONArray dataArray = dataOject.getJSONArray("data");
			auth.authenticateTimeStamp(token, dataOject.getLong("timestamp").longValue());
			
			JSONObject jsonRecord = dataArray.getJSONObject(0);
			AdUser adUser = JSONObject.toJavaObject(jsonRecord, AdUser.class);
			adUser.setStatus(0);
			int updateCount = adUserService.update(adUser);
			long timestamp = System.currentTimeMillis();
			jsonObject.put("code", 0);
			jsonObject.put("msg", "成功");
			jsonObject.put("count", 0);
			jsonObject.put("timestamp", timestamp);
			//jsonObject.put("data", new ArrayList());
			pureUser.setTimeStmap(timestamp);
			auth.setUserInfoByToken(token, pureUser);
		} catch (Exception e) {
			jsonObject = APIResponseUtil.makeErrorJSON(e);
		}
		response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");
}

	@ResponseBody
	@RequestMapping(value = "/rest/ad/getuser")
	public void getuser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject requestJson;
		JSONObject jsonObject = new JSONObject();
		try {
			String path = request.getRequestURI();
			requestJson = RequestUtils.getRequestJsonObject(request);
			String token = requestJson.getString("token");
			PureUser pureUser = auth.authenticateUser(token);
			String permStr = auth.getUserPermissionByAction(token, path, Auth.SELECT_ACTION);
			String dataStr = AES.aesDecrypt(requestJson.getString("data"),
					AES.complementKey(pureUser.getPassword(), 16));
			JSONObject dataOject = (JSONObject) JSONObject.parse(dataStr);
			JSONArray dataArray = dataOject.getJSONArray("data");
			auth.authenticateTimeStamp(token, dataOject.getLong("timestamp").longValue());

			int page = requestJson.getInteger("page");
			int limit = requestJson.getInteger("limit");
			AdUser adUser = JSONObject.toJavaObject(requestJson, AdUser.class);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			if (adUser.getUsername() != null && !adUser.getUsername().isEmpty())
				paramMap.put("username", adUser.getUsername());
			if (adUser.getCorp() != null && !adUser.getCorp().isEmpty())
				paramMap.put("corp", adUser.getCorp());
			if (adUser.getContact() != null && !adUser.getContact().isEmpty())
				paramMap.put("contact", adUser.getContact());
			// 用户权限
			if (permStr != null && permStr.equals("self"))
				paramMap.put("perm", adUser.getUsername());
			List<AdUser> adUserList = adUserService.findByParam(paramMap);
			List<AdUser> pageAdUserLits = new ArrayList<AdUser>();
			for (int i = (page - 1) * limit; i < adUserList.size() && i < page * limit; i++) {
				pageAdUserLits.add(adUserList.get(i));
			}
			for (AdUser a : pageAdUserLits) {
				JSONObject permObject = new JSONObject();
				permObject.put("addenable", auth.getRolePermission(token, path, "", a.getStatus(), Auth.INSERT_ACTION));
				permObject.put("delenable", auth.getRolePermission(token, path, "", a.getStatus(), Auth.DELETE_ACTION));
				permObject.put("eidtenable",
						auth.getRolePermission(token, path, "", a.getStatus(), Auth.UPDATE_ACTION));
				permObject.put("commitenable",
						auth.getRolePermission(token, path, "", a.getStatus(), Auth.COMMIT_ACTION));
				permObject.put("checkenable",
						auth.getRolePermission(token, path, "", a.getStatus(), Auth.CHECK_ACTION));
				a.setPerm(permObject);
			}

			jsonObject.put("code", 0);
			jsonObject.put("msg", "成功");
			jsonObject.put("count", adUserList.size());
			jsonObject.put("data", pageAdUserLits);

			JSONArray outdataArray = jsonObject.getJSONArray("data");
			String outdataStr = JSON.toJSONString(outdataArray);
			String DataEncrypt = AES.aesEncrypt(outdataStr, AES.complementKey(pureUser.getPassword(), 16));
			jsonObject.put("data", DataEncrypt);
		} catch (Exception e) {
			jsonObject = APIResponseUtil.makeErrorJSON(e);
		}
		response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");
	}

	@ResponseBody
	@RequestMapping(value = "/rest/ad/submitcheckuser")
	public void submitcheckuser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject requestJson;
		JSONObject jsonObject = new JSONObject();
		try {
			String path = request.getRequestURI();
			requestJson = RequestUtils.getRequestJsonObject(request);
			String token = requestJson.getString("token");
			PureUser pureUser = auth.authenticateUser(token);
			String permStr = auth.getUserPermissionByAction(token, path, Auth.COMMIT_ACTION);
			String dataStr = AES.aesDecrypt(requestJson.getString("data"),
					AES.complementKey(pureUser.getPassword(), 16));
			JSONObject dataOject = (JSONObject) JSONObject.parse(dataStr);
			JSONArray dataArray = dataOject.getJSONArray("data");
			auth.authenticateTimeStamp(token, dataOject.getLong("timestamp").longValue());

			List<AdUser> adUserList = new ArrayList<AdUser>();
			for (int i = 0; i < dataArray.size(); i++) {
				JSONObject o = dataArray.getJSONObject(i);
				AdUser adUser = JSONObject.toJavaObject(o, AdUser.class);
				boolean commitEnable = auth.getRolePermission(token, path, "", adUser.getStatus(), Auth.COMMIT_ACTION);
				if (commitEnable)
					adUserList.add(JSONObject.toJavaObject(o, AdUser.class));
			}
			int submitNum = adUserService.submitCheck(adUserList);
			Perm userPerm = auth.getUserPermission(token, path);
			jsonObject.put("code", 0);
			jsonObject.put("msg", "成功");
			jsonObject.put("count", submitNum);
			jsonObject.put("data", new ArrayList());
			jsonObject.put("perm", userPerm);

			JSONArray outdataArray = jsonObject.getJSONArray("data");
			String outdataStr = JSON.toJSONString(outdataArray);
			String DataEncrypt = AES.aesEncrypt(outdataStr, AES.complementKey(pureUser.getPassword(), 16));
			jsonObject.put("data", DataEncrypt);
		} catch (Exception e) {
			jsonObject = APIResponseUtil.makeErrorJSON(e);
		}
		response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");
	}
	
	@ResponseBody
	@RequestMapping(value = "/rest/ad/checkuserpass")
	public void checkuserpass(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject requestJson;
		JSONObject jsonObject = new JSONObject();
		try {
			String path = request.getRequestURI();
			requestJson = RequestUtils.getRequestJsonObject(request);
			String token = requestJson.getString("token");
			PureUser pureUser = auth.authenticateUser(token);
			String permStr = auth.getUserPermissionByAction(token, path, Auth.CHECK_ACTION);
			String dataStr = AES.aesDecrypt(requestJson.getString("data"),
					AES.complementKey(pureUser.getPassword(), 16));
			JSONObject dataOject = (JSONObject) JSONObject.parse(dataStr);
			JSONArray dataArray = dataOject.getJSONArray("data");
			auth.authenticateTimeStamp(token, dataOject.getLong("timestamp").longValue());

			List<AdUser> adUserList = new ArrayList<AdUser>();
			for (int i = 0; i < dataArray.size(); i++) {
				JSONObject o = dataArray.getJSONObject(i);
				AdUser adUser = JSONObject.toJavaObject(o, AdUser.class);
				boolean checkEnable = auth.getRolePermission(token, path, "", adUser.getStatus(), Auth.CHECK_ACTION);
				if (checkEnable){
					adUser.setCheckuser(pureUser.getUsername());
					adUser.setCheckdt(new Date());
					adUserList.add(adUser);
				}
			}
			int passNum = adUserService.checkPass(adUserList);
			Perm userPerm = auth.getUserPermission(token, path);
			jsonObject.put("code", 0);
			jsonObject.put("msg", "成功");
			jsonObject.put("count", passNum);
			jsonObject.put("data", new ArrayList());
			jsonObject.put("perm", userPerm);

			JSONArray outdataArray = jsonObject.getJSONArray("data");
			String outdataStr = JSON.toJSONString(outdataArray);
			String DataEncrypt = AES.aesEncrypt(outdataStr, AES.complementKey(pureUser.getPassword(), 16));
			jsonObject.put("data", DataEncrypt);
		} catch (Exception e) {
			jsonObject = APIResponseUtil.makeErrorJSON(e);
		}
		response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");
	}
	
	@ResponseBody
	@RequestMapping(value = "/rest/ad/checkuserfail")
	public void checkuserfail(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject requestJson;
		JSONObject jsonObject = new JSONObject();
		try {
			String path = request.getRequestURI();
			requestJson = RequestUtils.getRequestJsonObject(request);
			String token = requestJson.getString("token");
			PureUser pureUser = auth.authenticateUser(token);
			String permStr = auth.getUserPermissionByAction(token, path, Auth.CHECK_ACTION);
			String dataStr = AES.aesDecrypt(requestJson.getString("data"),
					AES.complementKey(pureUser.getPassword(), 16));
			JSONObject dataOject = (JSONObject) JSONObject.parse(dataStr);
			JSONArray dataArray = dataOject.getJSONArray("data");
			auth.authenticateTimeStamp(token, dataOject.getLong("timestamp").longValue());

			List<AdUser> adUserList = new ArrayList<AdUser>();
			for (int i = 0; i < dataArray.size(); i++) {
				JSONObject o = dataArray.getJSONObject(i);
				AdUser adUser = JSONObject.toJavaObject(o, AdUser.class);
				boolean checkEnable = auth.getRolePermission(token, path, "", adUser.getStatus(), Auth.CHECK_ACTION);
				if (checkEnable){
					adUser.setCheckuser(pureUser.getUsername());
					adUser.setCheckdt(new Date());
					adUserList.add(adUser);
				}
			}
			int passNum = adUserService.checkFail(adUserList);
			Perm userPerm = auth.getUserPermission(token, path);
			jsonObject.put("code", 0);
			jsonObject.put("msg", "成功");
			jsonObject.put("count", passNum);
			jsonObject.put("data", new ArrayList());
			jsonObject.put("perm", userPerm);

			JSONArray outdataArray = jsonObject.getJSONArray("data");
			String outdataStr = JSON.toJSONString(outdataArray);
			String DataEncrypt = AES.aesEncrypt(outdataStr, AES.complementKey(pureUser.getPassword(), 16));
			jsonObject.put("data", DataEncrypt);
		} catch (Exception e) {
			jsonObject = APIResponseUtil.makeErrorJSON(e);
		}
		response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");
	}

	@ResponseBody
	@RequestMapping(value = "/rest/ad/finduser")
	public void finduser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject requestJson;
		JSONObject jsonObject = new JSONObject();
		try {
			String path = request.getRequestURI();
			requestJson = RequestUtils.getRequestJsonObject(request);
			String token = requestJson.getString("token");
			PureUser pureUser = auth.authenticateUser(token);
			String permStr = auth.getUserPermissionByAction(token, path, Auth.SELECT_ACTION);
			String dataStr = AES.aesDecrypt(requestJson.getString("data"),
					AES.complementKey(pureUser.getPassword(), 16));
			JSONObject dataOject = (JSONObject) JSONObject.parse(dataStr);
			JSONArray dataArray = dataOject.getJSONArray("data");
			auth.authenticateTimeStamp(token, dataOject.getLong("timestamp").longValue());

			int page = requestJson.getInteger("page");
			int limit = requestJson.getInteger("limit");
			AdUser adUser = JSONObject.toJavaObject(dataArray.getJSONObject(0), AdUser.class);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			if (adUser.getUsername() != null && !adUser.getUsername().isEmpty())
				paramMap.put("username", adUser.getUsername());
			if (adUser.getCorp() != null && !adUser.getCorp().isEmpty())
				paramMap.put("corp", adUser.getCorp());
			if (adUser.getContact() != null && !adUser.getContact().isEmpty())
				paramMap.put("contact", adUser.getContact());
			// 用户权限
			if (permStr != null && permStr.equals("self"))
				paramMap.put("perm", adUser.getUsername());
			List<AdUser> adUserList = adUserService.findByParam(paramMap);
			List<AdUser> pageAdUserLits = new ArrayList<AdUser>();
			for (int i = (page - 1) * limit; i < adUserList.size() && i < page * limit; i++) {
				pageAdUserLits.add(adUserList.get(i));
			}
			for (AdUser a : pageAdUserLits) {
				JSONObject permObject = new JSONObject();
				permObject.put("addenable", auth.getRolePermission(token, path, "", a.getStatus(), Auth.INSERT_ACTION));
				permObject.put("delenable", auth.getRolePermission(token, path, "", a.getStatus(), Auth.DELETE_ACTION));
				permObject.put("eidtenable",
						auth.getRolePermission(token, path, "", a.getStatus(), Auth.UPDATE_ACTION));
				permObject.put("commitenable",
						auth.getRolePermission(token, path, "", a.getStatus(), Auth.COMMIT_ACTION));
				permObject.put("checkenable",
						auth.getRolePermission(token, path, "", a.getStatus(), Auth.CHECK_ACTION));
				a.setPerm(permObject);
			}

			jsonObject.put("code", 0);
			jsonObject.put("msg", "成功");
			jsonObject.put("count", adUserList.size());
			jsonObject.put("data", pageAdUserLits);

			JSONArray outdataArray = jsonObject.getJSONArray("data");
			String outdataStr = JSON.toJSONString(outdataArray);
			String DataEncrypt = AES.aesEncrypt(outdataStr, AES.complementKey(pureUser.getPassword(), 16));
			jsonObject.put("data", DataEncrypt);
		} catch (Exception e) {
			jsonObject = APIResponseUtil.makeErrorJSON(e);
		}
		response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");
	}

	@ResponseBody
	@RequestMapping(value = "/rest/ad/deluser")
	public void deluser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject requestJson;
		JSONObject jsonObject = new JSONObject();
		try {
			String path = request.getRequestURI();
			requestJson = RequestUtils.getRequestJsonObject(request);
			String token = requestJson.getString("token");
			PureUser pureUser = auth.authenticateUser(token);
			String permStr = auth.getUserPermissionByAction(token, path, Auth.DELETE_ACTION);
			String dataStr = AES.aesDecrypt(requestJson.getString("data"),
					AES.complementKey(pureUser.getPassword(), 16));
			JSONObject dataOject = (JSONObject) JSONObject.parse(dataStr);
			JSONArray dataArray = dataOject.getJSONArray("data");
			auth.authenticateTimeStamp(token, dataOject.getLong("timestamp").longValue());

			List<AdUser> adUserList = new ArrayList<AdUser>();
			for (int i = 0; i < dataArray.size(); i++) {
				JSONObject o = dataArray.getJSONObject(i);
				AdUser adUser = JSONObject.toJavaObject(o, AdUser.class);
				AdUser adUserInDB = adUserService.findByUserName(adUser.getUsername());
				boolean deleteEnable = auth.getRolePermission(token, path, "", adUserInDB.getStatus(),
						Auth.DELETE_ACTION);
				if (deleteEnable)
					adUserList.add(adUser);
			}
			int delCount = adUserService.deleteBatch(adUserList);

			long timestamp = System.currentTimeMillis();
			jsonObject.put("code", 0);
			jsonObject.put("msg", "成功");
			jsonObject.put("count", delCount);
			jsonObject.put("timestamp", timestamp);
			//jsonObject.put("data", new ArrayList());
			
			pureUser.setTimeStmap(timestamp);
			auth.setUserInfoByToken(token, pureUser);
		} catch (Exception e) {
			jsonObject = APIResponseUtil.makeErrorJSON(e);
		}
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
		jsonObject.put("code", 0);
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
		jsonObject.put("code", 0);
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
				mybatisRedisCache.putObject(watchkey, adUser);
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

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public static void main(String[] args) {
		String arry = "[{\"username\":\"1\",\"password\":\"2\"},{\"username\":\"2\",\"password\":\"22\"}]";
		JSONArray jsonarray = JSONObject.parseArray(arry);
		List<AdUser> adUserList = new ArrayList<AdUser>();
		AdUser adUser = JSONObject.toJavaObject(jsonarray.getJSONObject(0), AdUser.class);
		adUserList.add(adUser);
		adUser = JSONObject.toJavaObject(jsonarray.getJSONObject(1), AdUser.class);
		adUserList.add(adUser);
		// List<AdUser> adUserList = JSONObject.toJavaObject(jsonarray,
		// List.class);

		AdUser a = adUserList.get(1);

		System.out.println(((JSONObject) jsonarray.get(0)).get("aa").toString());

	}
}