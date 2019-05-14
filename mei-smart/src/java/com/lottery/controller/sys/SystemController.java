package com.lottery.controller.sys;

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
import com.lottery.model.auth.PureUser;
import com.lottery.model.sys.SysMenu;
import com.lottery.model.sys.SysRole;
import com.lottery.model.ad.AdContent;
import com.lottery.redis.MybatisRedisCache;
import com.lottery.schedule.ad.InsertAdPlayHisschedule;
import com.lottery.service.ad.AdContentService;
import com.lottery.service.ad.AdFeeService;
import com.lottery.service.ad.AdOnlineService;
import com.lottery.service.ad.AdPlayHisService;
import com.lottery.service.auth.Auth;
import com.lottery.service.sys.SysMenuService;
import com.lottery.service.sys.SysRoleService;
import com.lottery.service.ad.AdContentService;
import com.lottery.util.AES;
import com.lottery.util.APIResponseUtil;
import com.lottery.util.RequestUtils;

import redis.clients.jedis.Transaction;

@Controller
public class SystemController implements ApplicationContextAware {
	private static Logger logger = Logger.getLogger(SystemController.class);
	ExecutorService executor = Executors.newFixedThreadPool(1); // 1个线程池并发数
	@Autowired
	private AdContentService adContentService;
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private AdContentService AdContentService;
	@Autowired
	private Auth auth;
	@Autowired
	MybatisRedisCache mybatisRedisCache;
	ApplicationContext applicationContext;
	Semaphore feeSemaphore = new Semaphore(1, true);

	@ResponseBody
	@RequestMapping(value = "/rest/sys/menu")
	public void addcontent(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException, IOException {
		JSONObject requestJson;
		JSONObject jsonObject = new JSONObject();
		try {
			String path = request.getRequestURI();
			requestJson = RequestUtils.getRequestJsonObject(request);
			String token = requestJson.getString("token");
			PureUser pureUser = auth.authenticateUser(token);
			// String permStr = auth.getUserPermissionByAction(token, path,
			// Auth.INSERT_ACTION);
			String dataStr = AES.aesDecrypt(requestJson.getString("data"),
					AES.complementKey(pureUser.getPassword(), 16));
			JSONObject dataOject = (JSONObject) JSONObject.parse(dataStr);
			JSONArray dataArray = dataOject.getJSONArray("data");
			auth.authenticateTimeStamp(token, dataOject.getLong("timestamp").longValue());

			SysRole sysRole = sysRoleService.findByRoleId(pureUser.getRole_id());
			String[] MenuIds = sysRole.getMean().split(",");
			Integer[] MenuIdss = new Integer[MenuIds.length];
			for (int i = 0; i < MenuIds.length; i++) {
				MenuIdss[i] = Integer.valueOf(MenuIds[i]);
			}
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("MenuIds", MenuIdss);
			List<SysMenu> menuList = sysMenuService.findByMenuIds(param);
			jsonObject.put("code", 0);
			jsonObject.put("msg", "成功");
			jsonObject.put("count", 0);
			jsonObject.put("data", menuList);

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

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}