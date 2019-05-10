package com.lottery.service.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lottery.model.auth.Perm;
import com.lottery.model.auth.PureUser;
import com.lottery.model.sys.SysRolePerm;
import com.lottery.redis.MybatisRedisCache;
import com.lottery.service.sys.SysRolePermService;

@Service
public class Auth {
	final static long LOGIN_EXPIRETIME = 60 * 60;
	public final static String SELECT_ACTION = "读";
	public final static String INSERT_ACTION = "添加";
	public final static String UPDATE_ACTION = "更新";
	public final static String DELETE_ACTION = "删除";
	public final static String COMMIT_ACTION = "提交审核";
	public final static String CHECK_ACTION = "审核通过";
	@Autowired
	MybatisRedisCache mybatisRedisCache;
	@Autowired
	SysRolePermService sysRolePermService;

	public void setUserInfoByToken(String token, PureUser pureUser) {
		String PUREUSER_KEY = token + ":pureuser";
		mybatisRedisCache.putObject(PUREUSER_KEY, pureUser);
		mybatisRedisCache.expire(PUREUSER_KEY, LOGIN_EXPIRETIME);
	}

	public PureUser getUserInfoByToken(String token) {
		String PUREUSER_KEY = token + ":pureuser";
		PureUser pureUser = (PureUser) mybatisRedisCache.getObject(PUREUSER_KEY);
		if (mybatisRedisCache.ttl(PUREUSER_KEY) > 0)
			mybatisRedisCache.expire(PUREUSER_KEY, LOGIN_EXPIRETIME);
		return pureUser;
	}

	public PureUser authenticateUser(String token) throws Exception {
		PureUser pureUser = getUserInfoByToken(token);
		if (pureUser == null) {
			throw new Exception("{msg:\"用户未登录\",code:1001}");
		}
		return pureUser;
	}

	public PureUser authenticateTimeStamp(String token, long timeStamp) throws Exception {
		PureUser pureUser = getUserInfoByToken(token);
		if (pureUser == null) {
			throw new Exception("{msg:\"用户未登录\",code:1001}");
		} else if (pureUser.getTimeStmap() != timeStamp) {
			throw new Exception("{msg:\"时间戳无效\",code:-1}");
		}
		return pureUser;
	}

	public String getUserPermissionByAction(String token, String Path, String action) throws Exception {
		PureUser pureUser = getUserInfoByToken(token);
		if (pureUser == null) {
			throw new Exception("{msg:\"用户未登录\",code:1001}");
		}
		SysRolePerm sysRolePerm = sysRolePermService.findByRoleId(pureUser.getRole_id(), Path);
		if (sysRolePerm == null) {
			throw new Exception("{msg:\"用户无" + action + "权限\",code:-1}");
		}
		JSONObject JsonPerm = (JSONObject) JSONObject.parse(sysRolePerm.getPerm());
		Perm perm = JSONObject.toJavaObject(JsonPerm, Perm.class);
		if (action.equals(SELECT_ACTION)) {
			if (perm == null || perm.getSelect().isEmpty()) {
				throw new Exception("{msg:\"用户无" + action + "权限\",code:-1}");
			} else {
				return perm.getSelect();
			}
		} else if (action.equals(INSERT_ACTION)) {
			if (perm == null || perm.getInsert().isEmpty()) {
				throw new Exception("{msg:\"用户无" + action + "权限\",code:-1}");
			} else {
				return perm.getInsert();
			}
		} else if (action.equals(UPDATE_ACTION)) {
			if (perm == null || perm.getUpdate().isEmpty()) {
				throw new Exception("{msg:\"用户无" + action + "权限\",code:-1}");
			} else {
				return perm.getUpdate();
			}
		} else if (action.equals(DELETE_ACTION)) {
			if (perm == null || perm.getDelete().isEmpty()) {
				throw new Exception("{msg:\"用户无" + action + "权限\",code:-1}");
			} else {
				return perm.getDelete();
			}
		} else if (action.equals(COMMIT_ACTION)) {
			if (perm == null || perm.getCommit().isEmpty()) {
				throw new Exception("{msg:\"用户无" + action + "权限\",code:-1}");
			} else {
				return perm.getDelete();
			}
		} else if (action.equals(CHECK_ACTION)) {
			if (perm == null || perm.getCheck().isEmpty()) {
				throw new Exception("{msg:\"用户无" + action + "权限\",code:-1}");
			} else {
				return perm.getDelete();
			}
		}

		return null;
	}

	public boolean getRolePermission(String token, String Path, String owner, Integer status, String action)
			throws Exception {
		PureUser pureUser = getUserInfoByToken(token);
		if (pureUser == null) {
			throw new Exception("{msg:\"用户未登录\",code:1001}");
		}
		SysRolePerm sysRolePerm = sysRolePermService.findByRoleId(pureUser.getRole_id(), Path);
		if (sysRolePerm == null) {
			return false;
		}
		JSONObject JsonPerm = (JSONObject) JSONObject.parse(sysRolePerm.getPerm());
		Perm perm = JSONObject.toJavaObject(JsonPerm, Perm.class);
		if (perm == null)
			return false;
		if (status == 0) {
			if (action.equals(SELECT_ACTION)) {
				if (perm == null || perm.getSelect().isEmpty()) {
					return false;
				} else if (perm.getSelect().equals("self") && !owner.equals(pureUser.getUsername())) {
					return false;
				} else
					return true;
			} else if (action.equals(INSERT_ACTION)) {
				if (perm == null || perm.getInsert().isEmpty()) {
					return false;
				} else if (perm.getSelect().equals("self") && !owner.equals(pureUser.getUsername())) {
					return false;
				} else
					return true;
			} else if (action.equals(UPDATE_ACTION)) {
				if (perm == null || perm.getUpdate().isEmpty()) {
					return false;
				} else if (perm.getSelect().equals("self") && !owner.equals(pureUser.getUsername())) {
					return false;
				} else
					return true;
			} else if (action.equals(DELETE_ACTION)) {
				if (perm == null || perm.getDelete().isEmpty()) {
					return false;
				} else if (perm.getSelect().equals("self") && !owner.equals(pureUser.getUsername())) {
					return false;
				} else
					return true;
			} else if (action.equals(COMMIT_ACTION)) {
				if (perm == null || perm.getCommit().isEmpty()) {
					return false;
				} else if (perm.getCommit().equals("self") && !owner.equals(pureUser.getUsername())) {
					return false;
				} else
					return true;
			} else if (action.equals(CHECK_ACTION)) {
				if (perm == null || perm.getCheck().isEmpty()) {
					return false;
				} else if (perm.getCheck().equals("self") && !owner.equals(pureUser.getUsername())) {
					return false;
				} else
					return false;
			}
		} else if (status == 1) {
			if (action.equals(SELECT_ACTION)) {
				if (perm == null || perm.getSelect().isEmpty()) {
					return false;
				} else if (perm.getSelect().equals("self") && !owner.equals(pureUser.getUsername())) {
					return false;
				} else
					return true;
			} else if (action.equals(INSERT_ACTION)) {
				if (perm == null || perm.getInsert().isEmpty()) {
					return false;
				} else if (perm.getSelect().equals("self") && !owner.equals(pureUser.getUsername())) {
					return false;
				} else
					return true;
			} else if (action.equals(UPDATE_ACTION)) {
				if (perm == null || perm.getUpdate().isEmpty()) {
					return false;
				} else if (perm.getSelect().equals("self") && !owner.equals(pureUser.getUsername())) {
					return false;
				} else
					return false;
			} else if (action.equals(DELETE_ACTION)) {
				if (perm == null || perm.getDelete().isEmpty()) {
					return false;
				} else if (perm.getSelect().equals("self") && !owner.equals(pureUser.getUsername())) {
					return false;
				} else
					return false;
			} else if (action.equals(COMMIT_ACTION)) {
				if (perm == null || perm.getCommit().isEmpty()) {
					return false;
				} else if (perm.getCommit().equals("self") && !owner.equals(pureUser.getUsername())) {
					return false;
				} else
					return false;
			} else if (action.equals(CHECK_ACTION)) {
				if (perm == null || perm.getCheck().isEmpty()) {
					return false;
				} else if (perm.getCheck().equals("self") && !owner.equals(pureUser.getUsername())) {
					return false;
				} else
					return true;
			}
		//status=2的状态只能编辑后才能提交
		} else if (status == 2) {
			if (action.equals(SELECT_ACTION)) {
				if (perm == null || perm.getSelect().isEmpty()) {
					return false;
				} else if (perm.getSelect().equals("self") && !owner.equals(pureUser.getUsername())) {
					return false;
				} else
					return true;
			} else if (action.equals(INSERT_ACTION)) {
				if (perm == null || perm.getInsert().isEmpty()) {
					return false;
				} else if (perm.getSelect().equals("self") && !owner.equals(pureUser.getUsername())) {
					return false;
				} else
					return true;
			} else if (action.equals(UPDATE_ACTION)) {
				if (perm == null || perm.getUpdate().isEmpty()) {
					return false;
				} else if (perm.getSelect().equals("self") && !owner.equals(pureUser.getUsername())) {
					return false;
				} else
					return true;
			} else if (action.equals(DELETE_ACTION)) {
				if (perm == null || perm.getDelete().isEmpty()) {
					return false;
				} else if (perm.getSelect().equals("self") && !owner.equals(pureUser.getUsername())) {
					return false;
				} else
					return true;
			} else if (action.equals(COMMIT_ACTION)) {
				if (perm == null || perm.getCommit().isEmpty()) {
					return false;
				} else if (perm.getCommit().equals("self") && !owner.equals(pureUser.getUsername())) {
					return false;
				} else
					return false;
			} else if (action.equals(CHECK_ACTION)) {
				if (perm == null || perm.getCheck().isEmpty()) {
					return false;
				} else if (perm.getCheck().equals("self") && !owner.equals(pureUser.getUsername())) {
					return false;
				} else
					return false;
			}
		}
		return false;
	}

	public Perm getUserPermission(String token, String Path) throws Exception {
		PureUser pureUser = getUserInfoByToken(token);
		if (pureUser == null) {
			throw new Exception("{msg:\"用户未登录\",code:1001}");
		}
		SysRolePerm sysRolePerm = sysRolePermService.findByRoleId(pureUser.getRole_id(), Path);
		return JSONObject.toJavaObject(JSONObject.parseObject(sysRolePerm.getPerm()), Perm.class);

	}
}