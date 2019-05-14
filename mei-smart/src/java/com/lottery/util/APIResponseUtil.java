package com.lottery.util;

import java.util.ArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class APIResponseUtil {

	public static JSONObject makeErrorJSON(Exception error) {
		JSONObject jsonObject = new JSONObject();
		try {
			//jsonObject.put("count", 0);
			//jsonObject.put("data", new ArrayList<>());
			JSONObject jsonMsg = (JSONObject) JSON.parse(error.getMessage());
			jsonObject.put("code", jsonMsg.getInteger("code"));
			jsonObject.put("msg", jsonMsg.getString("msg"));			
		} catch (Exception e) {
			jsonObject.put("code", -1);
			jsonObject.put("msg", error.getMessage());
		}
		return jsonObject;
	}
}