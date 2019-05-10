package com.lottery.util;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;

import com.alibaba.fastjson.JSONObject;


public class RequestUtils {

	public static JSONObject getRequestJsonObject(HttpServletRequest request) throws IOException {
		String json = getRequestJsonString(request);
		json= StringEscapeUtils.unescapeJavaScript(json);
		return (JSONObject) JSONObject.parse(json);
	}

	public static String getRequestJsonString(HttpServletRequest request) throws IOException {
		String submitMehtod = request.getMethod();
		// GET
		if (submitMehtod.equals("GET")) {
			String s = new String(request.getQueryString().getBytes("iso-8859-1"), "utf-8");//。replaceAll("%22", "\"");
			return URLDecoder.decode(s);
			// POST
		} else {
			return getRequestPostStr(request);
		}
	}

	public static byte[] getRequestPostBytes(HttpServletRequest request) throws IOException {
		int contentLength = request.getContentLength();
		if (contentLength < 0) {
			return null;
		}
		byte buffer[] = new byte[contentLength];
		for (int i = 0; i < contentLength;) {

			int readlen = request.getInputStream().read(buffer, i, contentLength - i);
			if (readlen == -1) {
				break;
			}
			i += readlen;
		}
		return buffer;
	}

	public static String getRequestPostStr(HttpServletRequest request) throws IOException {
		byte buffer[] = getRequestPostBytes(request);
		//return new String(buffer);
		String charEncoding = request.getCharacterEncoding();
		if (charEncoding == null) {
			charEncoding = "UTF-8";
		}
		return new String(buffer, charEncoding);
	}

}