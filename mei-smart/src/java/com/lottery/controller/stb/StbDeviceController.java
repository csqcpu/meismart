package com.lottery.controller.stb;

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
import com.lottery.model.stb.StbModel;
import com.lottery.redis.MybatisRedisCache;
import com.lottery.schedule.ad.InsertAdPlayHisschedule;
import com.lottery.service.stb.StbModelService;
import com.lottery.service.ad.AdFeeService;
import com.lottery.service.ad.AdOnlineService;
import com.lottery.service.ad.AdPlayHisService;
import com.lottery.service.auth.Auth;
import com.lottery.service.sys.SysRolePermService;
import com.lottery.service.stb.StbModelService;
import com.lottery.util.AES;
import com.lottery.util.APIResponseUtil;
import com.lottery.util.RequestUtils;

import redis.clients.jedis.Transaction;

@Controller
public class StbDeviceController implements ApplicationContextAware {
	private static Logger logger = Logger.getLogger(StbDeviceController.class);
	ExecutorService executor = Executors.newFixedThreadPool(1); // 1个线程池并发数
	@Autowired
	private StbModelService stbModelService;
	@Autowired
	private Auth auth;
	@Autowired
	private StbModelService StbModelService;
	@Autowired
	private AdPlayHisService adPlayHisService;
	@Autowired
	SysRolePermService sysRolePermService;
	@Autowired
	MybatisRedisCache mybatisRedisCache;
	ApplicationContext applicationContext;
	Semaphore feeSemaphore = new Semaphore(1, true);
	
	
	@ResponseBody
	@RequestMapping(value = "/rest/stb/stbmodel")
	public void stbmodel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject requestJson;
		JSONObject jsonObject = new JSONObject();
		try {
//			String path = request.getRequestURI();
//			requestJson = RequestUtils.getRequestJsonObject(request);
//			String token = requestJson.getString("token");
//			PureUser pureUser = auth.authenticateUser(token);
//			String permStr = auth.getUserPermissionByAction(token, path, Auth.SELECT_ACTION);
//			String dataStr = AES.aesDecrypt(requestJson.getString("data"),
//					AES.complementKey(pureUser.getPassword(), 16));
//			JSONObject dataOject = (JSONObject) JSONObject.parse(dataStr);
//			JSONArray dataArray = dataOject.getJSONArray("data");
//			auth.authenticateTimeStamp(token, dataOject.getLong("timestamp").longValue());
//			int page = requestJson.getInteger("page");
//			int limit = requestJson.getInteger("limit");

			List<StbModel> StbModelList = stbModelService.findAll();
			jsonObject.put("code", 0);
			jsonObject.put("msg", "成功");
			jsonObject.put("count", StbModelList.size());
			jsonObject.put("data", StbModelList);

//			JSONArray outdataArray = jsonObject.getJSONArray("data");
//			String outdataStr = dataArray.toJSONString(outdataArray);
//			String DataEncrypt = AES.aesEncrypt(outdataStr, AES.complementKey(pureUser.getPassword(), 16));
//			jsonObject.put("data", DataEncrypt);
		} catch (Exception e) {
			jsonObject = APIResponseUtil.makeErrorJSON(e);
		}
		response.getOutputStream().write(jsonObject.toString().getBytes("UTF-8"));
		response.setContentType("text/json; charset=UTF-8");
	}

	@ResponseBody
	@RequestMapping(value = "/rest/stb/getstbmodel")
	public void getstbmodel(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

			StbModel stbModel = JSONObject.toJavaObject(requestJson, StbModel.class);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			if (stbModel.getStb_id() != null)
				paramMap.put("stbmodel_id", stbModel.getStb_id());
			if (stbModel.getName() != null && !stbModel.getName().isEmpty())
				paramMap.put("title", stbModel.getName());
			if (stbModel.getModel() != null && !stbModel.getModel().isEmpty())
				paramMap.put("description", stbModel.getModel());
			if (stbModel.getStatus() != null)
				paramMap.put("status", stbModel.getStatus());
			// 用户权限
			if (permStr != null && permStr.equals("self"))
				paramMap.put("perm", pureUser.getUsername());
			List<StbModel> StbModelList = stbModelService.findByParam(paramMap);
			for (StbModel a : StbModelList) {
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
			jsonObject.put("count", StbModelList.size());
			jsonObject.put("data", StbModelList);
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
	@RequestMapping(value = "/rest/stb/delstbmodel")
	public void delstbmodel(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

			List<StbModel> stbModelList = new ArrayList<StbModel>();
			for (int i = 0; i < dataArray.size(); i++) {
				JSONObject o = dataArray.getJSONObject(i);
				StbModel stbModel = JSONObject.toJavaObject(o, StbModel.class);
				StbModel stbModelInDB = stbModelService.findById(stbModel.getStb_id());
				boolean deleteEnable = auth.getRolePermission(token, path, stbModelInDB.getCreateuser(), stbModelInDB.getStatus(),
						Auth.DELETE_ACTION);
				if (deleteEnable)
					stbModelList.add(stbModel);
			}
			int delCount = stbModelService.delByIds(stbModelList);

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
	@RequestMapping(value = "/rest/stb/updatestbmodel")
	public void updatestbmodel(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
			StbModel stbModel = JSONObject.toJavaObject(jsonRecord, StbModel.class);
			StbModel stbModelInDB = stbModelService.findById(stbModel.getStb_id());
			boolean updateEnable = auth.getRolePermission(token, path, stbModelInDB.getCreateuser(),
					stbModelInDB.getStatus(), Auth.UPDATE_ACTION);
			if (!updateEnable) {
				throw new Exception("{msg:\"用户无" + Auth.UPDATE_ACTION + "权限\",code:-1}");
			}
			stbModel.setStatus(0);
			int updateCount = stbModelService.update(stbModel);
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
	@RequestMapping(value = "/rest/stb/submitcheckstbmodel")
	public void submitcheckstbmodel(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

			List<StbModel> stbModelList = new ArrayList<StbModel>();
			for (int i = 0; i < dataArray.size(); i++) {
				JSONObject o = dataArray.getJSONObject(i);
				StbModel stbModel = JSONObject.toJavaObject(o, StbModel.class);
				StbModel stbModelInDB = stbModelService.findById(stbModel.getStb_id());
				boolean commitEnable = auth.getRolePermission(token, path, stbModelInDB.getCreateuser(),
						stbModelInDB.getStatus(), Auth.COMMIT_ACTION);
				if (commitEnable)
					stbModelList.add(JSONObject.toJavaObject(o, StbModel.class));
			}
			int submitNum = stbModelService.submitCheckByIds(stbModelList);
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
	@RequestMapping(value = "/rest/stb/checkstbmodelpass")
	public void checkstbmodelpass(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

			List<StbModel> stbModelList = new ArrayList<StbModel>();
			for (int i = 0; i < dataArray.size(); i++) {
				JSONObject o = dataArray.getJSONObject(i);
				StbModel stbModel = JSONObject.toJavaObject(o, StbModel.class);
				StbModel stbModelInDB = stbModelService.findById(stbModel.getStb_id());
				boolean checkEnable = auth.getRolePermission(token, path, stbModelInDB.getCheckuser(), stbModelInDB.getStatus(), Auth.CHECK_ACTION);
				if (checkEnable) {
					stbModel.setCheckuser(pureUser.getUsername());
					stbModel.setCheckdt(new Date());
					stbModelList.add(stbModel);
				}
			}
			int passNum = stbModelService.checkPassByIds(stbModelList);
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
	@RequestMapping(value = "/rest/stb/checkstbmodelfail")
	public void checkstbmodelfail(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

			List<StbModel> stbModelList = new ArrayList<StbModel>();
			for (int i = 0; i < dataArray.size(); i++) {
				JSONObject o = dataArray.getJSONObject(i);
				StbModel stbModel = JSONObject.toJavaObject(o, StbModel.class);
				StbModel stbModelInDB = stbModelService.findById(stbModel.getStb_id());
				boolean checkEnable = auth.getRolePermission(token, path, stbModelInDB.getCreateuser(), stbModelInDB.getStatus(), Auth.CHECK_ACTION);
				if (checkEnable) {
					stbModel.setCheckuser(pureUser.getUsername());
					stbModel.setCheckdt(new Date());
					stbModelList.add(stbModel);
				}
			}
			int passNum = stbModelService.checkFailByIds(stbModelList);
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