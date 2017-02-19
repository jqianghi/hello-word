package cn.e3mall.sso.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.po.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.manager.po.TbUser;
import cn.e3mall.sso.service.SsoService;
import cn.e3mall.sso.utils.CookieUtils;

@RequestMapping("/sso")
@Controller
public class SsoController {

	@RequestMapping("/showRegister")
	public String showRegister() {
		return "register";
	}

	@Autowired
	private SsoService service;

	@RequestMapping("/register")
	@ResponseBody
	public E3Result register(TbUser user) {
		return service.register(user);
	}

	@RequestMapping("/showLogin")
	public String showLogin(String redirectUrl,Model model) {
		
		//如果重定向URL不为空,直接放入request域中,key为redirect
		if (StringUtils.isNotEmpty(redirectUrl)) {
			model.addAttribute("redirect", redirectUrl);
		}
		return "login";
	}

	@Value("${TT_TOKEN}")
	private String TT_TOKEN;

	@RequestMapping("/login")
	@ResponseBody
	public E3Result login(String username, String password, HttpServletRequest request, HttpServletResponse response) {
		// 返回登录成功信息
		E3Result result = service.login(username, password);
		// 将业务返回的token写入cookie中
		if (result.getData() == null) {
			return result;
		}
		CookieUtils.setCookie(request, response, TT_TOKEN, result.getData().toString(), true);
		return result;
	}

	/*
	 * @RequestMapping("/token/{token}")
	 * 
	 * @ResponseBody public E3Result checkLogin(@PathVariable String token){
	 * return service.checkLogin(token); }
	 */

	@RequestMapping("/token/{token}")
	@ResponseBody
	public String checkLogin(@PathVariable String token, String callback) {

		E3Result result = service.checkLogin(token);
		if (StringUtils.isEmpty(callback)) {
			return JsonUtils.objectToJson(result);
		}
		// js跨域处理,需要返回js脚本格式的字符串
		return callback + "(" + JsonUtils.objectToJson(result) + ")";
	}

	/**
	 * 演示js跨域原理
	 * 
	 * @param test
	 * @param callback
	 * @return
	 */
	@RequestMapping("/test/{test}")
	@ResponseBody
	public String test(@PathVariable String test, String callback) {
		TbUser user = new TbUser();
		user.setId(1L);
		user.setUsername("zhangsan");

		return callback + "(" + JsonUtils.objectToJson(user) + ")";
	}
}
