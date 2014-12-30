package com.lin.action.base;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 
 * @author hackcoder
 *
 */
public class BaseAction {

	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected HttpSession session;

	/**
	 * 每次action方法调用之前调用，设置HttpServletRequest和HttpServletResponse
	 * 
	 * @param request
	 * @param response
	 */
	@ModelAttribute
	public void setReqAndRes(HttpServletRequest request,
			HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.session = request.getSession();
	}

	protected HttpServletRequest getRequest() {
		return request;
	}

	protected HttpServletResponse getResponse() {
		return response;
	}

	protected HttpSession getSession() {
		return session;
	}

	/**
	 * 取得访问域名
	 * 
	 * @return
	 */
	public String getURL() {
		return getRequest().getServerName();
	}

	/**
	 * 存放Cookie
	 * 
	 * @param name
	 * @param value
	 */
	public void addCookie(String name, String value) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(60 * 60 * 24);
		cookie.setPath("/");
		getResponse().addCookie(cookie);
	}

	/**
	 * 删除Cookie
	 * 
	 * @param name
	 * @param value
	 */
	public void removeCookie(String name) {
		Cookie cookie = new Cookie(name, null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		getResponse().addCookie(cookie);
	}

	/**
	 * Cookie取得
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getCookie(String name) {
		try {
			Cookie[] cookies = getRequest().getCookies();
			for (Cookie cookie : cookies) {
				cookie.setPath("/");
				if (cookie.getName().equals(name)) {
					return cookie.getValue();
				}
			}
		} catch (Exception e) {
		}
		return "";
	}

	/**
	 * 获取IP地址
	 */
	public String getIpAddress() {
		HttpServletRequest request = getRequest();
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
