package cn.e3mall.order.controller.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.common.po.E3Result;
import cn.e3mall.order.utils.CookieUtils;
import cn.e3mall.sso.service.SsoService;

public class LoginInterceptor implements HandlerInterceptor {

	@Value("${TT_TOKEN}")
	private String TT_TOKEN;

	@Value("${SSO_LOGIN_URL}")
	private String SSO_LOGIN_URL;

	@Autowired
	private SsoService ssoService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 获取登录令牌token
		String token = CookieUtils.getCookieValue(request, TT_TOKEN, true);
		// 如果token为空,跳转到登录页面,并且带上订单确认页的请求URL
		if (StringUtils.isEmpty(token)) {
			// 获取请求URL
			String redirectUrl = request.getRequestURL().toString();
			response.sendRedirect(SSO_LOGIN_URL + redirectUrl);

			return false;
		}
		// 根据token查询登录用户信息
		E3Result result = ssoService.checkLogin(token);
		// 如果用户信息为空,跳转到登录页面,并且带上订单确认页的请求URL
		if (result.getData() == null) {
			// 获取请求URL
			String redirectUrl = request.getRequestURL().toString();
			response.sendRedirect(SSO_LOGIN_URL + redirectUrl);

			return false;
		}
		// 如果登录,需要将用户信息写入request域中,key为user
		request.setAttribute("user", result.getData());
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
