package com.lin.action;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lin.action.base.BaseAction;
import com.qq.connect.oauth.Oauth;


@Controller
@RequestMapping(value = "/")
public class IndexAction extends BaseAction{

	@Autowired
	HttpServletRequest request;

	@RequestMapping(value = "/qqAuth")
	public String qqAuth() throws Exception {
		String url = new Oauth().getAuthorizeURL(request);
		System.out.println("========================url======================"+url);
		return "redirect:" + url;
	}
	
	@RequestMapping(value="/qq")
	public String qq(){
		return "callback";
	}
}
