package com.lottery.controller.ad;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.lottery.model.sys.SysRolePerm;
import com.lottery.model.ad.AdContent;
import com.lottery.redis.MybatisRedisCache;
import com.lottery.schedule.ad.InsertAdPlayHisschedule;
import com.lottery.service.ad.AdContentService;
import com.lottery.service.ad.AdFeeService;
import com.lottery.service.ad.AdOnlineService;
import com.lottery.service.ad.AdPlayHisService;
import com.lottery.service.auth.Auth;
import com.lottery.service.sys.SysRolePermService;
import com.lottery.service.ad.AdContentService;
import com.lottery.util.AES;
import com.lottery.util.APIResponseUtil;
import com.lottery.util.RequestUtils;

import redis.clients.jedis.Transaction;

@Controller
public class AdContentController implements ApplicationContextAware {
	private static Logger logger = Logger.getLogger(AdContentController.class);
	ExecutorService executor = Executors.newFixedThreadPool(1); // 1个线程池并发数
	@Autowired
	private AdContentService adContentService;
	@Autowired
	private Auth auth;
	@Autowired
	private AdContentService AdContentService;
	@Autowired
	private AdPlayHisService adPlayHisService;
	@Autowired
	SysRolePermService sysRolePermService;
	@Autowired
	MybatisRedisCache mybatisRedisCache;
	ApplicationContext applicationContext;
	Semaphore feeSemaphore = new Semaphore(1, true);

	@ResponseBody
	@RequestMapping(value = "/rest/ad/addcontent")
	public void addcontent(HttpServletRequest request, HttpServletResponse response)
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
			AdContent adContent = JSONObject.toJavaObject(jsonRecord, AdContent.class);
			adContent.setStatus(0);
			adContent.setCreateuser(pureUser.getUsername());
			adContent.setCreatedt(new Date());
			int insertCount = AdContentService.insert(adContent);
			long timestamp = System.currentTimeMillis();
			jsonObject.put("code", 0);
			jsonObject.put("msg", "成功");
			jsonObject.put("count", insertCount);
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
	@RequestMapping(value = "/rest/ad/getcontent")
	public void getcontent(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

			AdContent adContent = JSONObject.toJavaObject(requestJson, AdContent.class);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			if (adContent.getContent_id() != null)
				paramMap.put("content_id", adContent.getContent_id());
			if (adContent.getTitle() != null && !adContent.getTitle().isEmpty())
				paramMap.put("title", adContent.getTitle());
			if (adContent.getDescription() != null && !adContent.getDescription().isEmpty())
				paramMap.put("description", adContent.getDescription());
			if (adContent.getStatus() != null)
				paramMap.put("status", adContent.getStatus());
			// 用户权限
			if (permStr != null && permStr.equals("self"))
				paramMap.put("perm", pureUser.getUsername());
			List<AdContent> AdContentList = adContentService.findByParam(paramMap);
			for (AdContent a : AdContentList) {
				JSONObject permObject = new JSONObject();
				permObject.put("addenable",
						auth.getRolePermission(token, path, a.getCreateuser(), a.getStatus(), Auth.INSERT_ACTION));
				permObject.put("delenable",
						auth.getRolePermission(token, path, a.getCreateuser(), a.getStatus(), Auth.DELETE_ACTION));
				permObject.put("eidtenable",
						auth.getRolePermission(token, path, a.getCreateuser(), a.getStatus(), Auth.UPDATE_ACTION));
				permObject.put("commitenable",
						auth.getRolePermission(token, path, a.getCreateuser(), a.getStatus(), Auth.COMMIT_ACTION));
				permObject.put("checkenable",
						auth.getRolePermission(token, path, a.getCreateuser(), a.getStatus(), Auth.CHECK_ACTION));
				a.setPerm(permObject);
			}
			Perm userPerm = auth.getUserPermission(token, path);
			jsonObject.put("code", 0);
			jsonObject.put("msg", "成功");
			jsonObject.put("count", AdContentList.size());
			jsonObject.put("data", AdContentList);
			jsonObject.put("perm", userPerm);

			JSONArray outdataArray = jsonObject.getJSONArray("data");
			String outdataStr = dataArray.toJSONString(outdataArray);
			String DataEncrypt = AES.aesEncrypt(outdataStr, AES.complementKey(pureUser.getPassword(), 16));
			jsonObject.put("data", DataEncrypt);
		} catch (Exception e) {
			jsonObject = APIResponseUtil.makeErrorJSON(e);
		}
		response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");
	}

	@ResponseBody
	@RequestMapping(value = "/rest/ad/delcontent")
	public void delcontent(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

			List<AdContent> adContentList = new ArrayList<AdContent>();
			for (int i = 0; i < dataArray.size(); i++) {
				JSONObject o = dataArray.getJSONObject(i);
				AdContent adContent = JSONObject.toJavaObject(o, AdContent.class);
				AdContent adContentInDB = adContentService.findById(adContent.getContent_id());
				boolean deleteEnable = auth.getRolePermission(token, path, adContentInDB.getCreateuser(), adContentInDB.getStatus(),
						Auth.DELETE_ACTION);
				if (deleteEnable)
					adContentList.add(adContent);
			}
			int delCount = adContentService.delByIds(adContentList);

			long timestamp = System.currentTimeMillis();
			jsonObject.put("code", 0);
			jsonObject.put("msg", "成功");
			jsonObject.put("count", delCount);
			jsonObject.put("timestamp", timestamp);
			// jsonObject.put("data", new ArrayList());

			pureUser.setTimeStmap(timestamp);
			auth.setUserInfoByToken(token, pureUser);
		} catch (Exception e) {
			jsonObject = APIResponseUtil.makeErrorJSON(e);
		}
		response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");
	}

	@ResponseBody
	@RequestMapping(value = "/rest/ad/updatecontent")
	public void updatecontent(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
			AdContent adContent = JSONObject.toJavaObject(jsonRecord, AdContent.class);
			AdContent adContentInDB = adContentService.findById(adContent.getContent_id());
			boolean updateEnable = auth.getRolePermission(token, path, adContentInDB.getCreateuser(),
					adContentInDB.getStatus(), Auth.UPDATE_ACTION);
			if (!updateEnable) {
				throw new Exception("{msg:\"用户无" + Auth.UPDATE_ACTION + "权限\",code:-1}");
			}
			adContent.setStatus(0);
			int updateCount = adContentService.update(adContent);
			long timestamp = System.currentTimeMillis();
			jsonObject.put("code", 0);
			jsonObject.put("msg", "成功");
			jsonObject.put("count", updateCount);
			jsonObject.put("timestamp", timestamp);

			pureUser.setTimeStmap(timestamp);
			auth.setUserInfoByToken(token, pureUser);
		} catch (Exception e) {
			jsonObject = APIResponseUtil.makeErrorJSON(e);
		}
		response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");
	}

	@ResponseBody
	@RequestMapping(value = "/rest/ad/submitcheckcontent")
	public void submitcheckcontent(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

			List<AdContent> adContentList = new ArrayList<AdContent>();
			for (int i = 0; i < dataArray.size(); i++) {
				JSONObject o = dataArray.getJSONObject(i);
				AdContent adContent = JSONObject.toJavaObject(o, AdContent.class);
				AdContent adContentInDB = adContentService.findById(adContent.getContent_id());
				boolean commitEnable = auth.getRolePermission(token, path, adContentInDB.getCreateuser(),
						adContentInDB.getStatus(), Auth.COMMIT_ACTION);
				if (commitEnable)
					adContentList.add(JSONObject.toJavaObject(o, AdContent.class));
			}
			int submitNum = adContentService.submitCheckByIds(adContentList);
			long timestamp = System.currentTimeMillis();
			Perm userPerm = auth.getUserPermission(token, path);
			jsonObject.put("code", 0);
			jsonObject.put("msg", "成功");
			jsonObject.put("count", submitNum);
			jsonObject.put("data", new ArrayList());
			jsonObject.put("perm", userPerm);
			jsonObject.put("timestamp", timestamp);

			pureUser.setTimeStmap(timestamp);
			auth.setUserInfoByToken(token, pureUser);
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
	@RequestMapping(value = "/rest/ad/checkcontentpass")
	public void checkcontentpass(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

			List<AdContent> adContentList = new ArrayList<AdContent>();
			for (int i = 0; i < dataArray.size(); i++) {
				JSONObject o = dataArray.getJSONObject(i);
				AdContent adContent = JSONObject.toJavaObject(o, AdContent.class);
				AdContent adContentInDB = adContentService.findById(adContent.getContent_id());
				boolean checkEnable = auth.getRolePermission(token, path, adContentInDB.getCheckuser(), adContentInDB.getStatus(), Auth.CHECK_ACTION);
				if (checkEnable) {
					adContent.setCheckuser(pureUser.getUsername());
					adContent.setCheckdt(new Date());
					adContentList.add(adContent);
				}
			}
			int passNum = adContentService.checkPassByIds(adContentList);
			long timestamp = System.currentTimeMillis();
			Perm userPerm = auth.getUserPermission(token, path);
			jsonObject.put("code", 0);
			jsonObject.put("msg", "成功");
			jsonObject.put("count", passNum);
			jsonObject.put("data", new ArrayList());
			jsonObject.put("perm", userPerm);
			jsonObject.put("timestamp", timestamp);
			
			pureUser.setTimeStmap(timestamp);
			auth.setUserInfoByToken(token, pureUser);
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
	@RequestMapping(value = "/rest/ad/checkcontentfail")
	public void checkcontentfail(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

			List<AdContent> adContentList = new ArrayList<AdContent>();
			for (int i = 0; i < dataArray.size(); i++) {
				JSONObject o = dataArray.getJSONObject(i);
				AdContent adContent = JSONObject.toJavaObject(o, AdContent.class);
				AdContent adContentInDB = adContentService.findById(adContent.getContent_id());
				boolean checkEnable = auth.getRolePermission(token, path, adContentInDB.getCreateuser(), adContentInDB.getStatus(), Auth.CHECK_ACTION);
				if (checkEnable) {
					adContent.setCheckuser(pureUser.getUsername());
					adContent.setCheckdt(new Date());
					adContentList.add(adContent);
				}
			}
			int passNum = adContentService.checkFailByIds(adContentList);
			long timestamp = System.currentTimeMillis();
			Perm userPerm = auth.getUserPermission(token, path);
			jsonObject.put("code", 0);
			jsonObject.put("msg", "成功");
			jsonObject.put("count", passNum);
			jsonObject.put("data", new ArrayList());
			jsonObject.put("timestamp", timestamp);
			jsonObject.put("perm", userPerm);
			pureUser.setTimeStmap(timestamp);
			auth.setUserInfoByToken(token, pureUser);
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

	public JSONObject getRolePathPermission(Integer roleId, String path) {
		try {
			SysRolePerm sysRolePerm = sysRolePermService.findByRoleId(roleId, path);
			return (JSONObject) JSONObject.parse(sysRolePerm.getPerm());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}