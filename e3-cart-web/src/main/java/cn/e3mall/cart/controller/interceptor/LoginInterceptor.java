package cn.e3mall.cart.controller.interceptor;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.cart.utils.CookieUtils;
import cn.e3mall.common.po.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.manager.po.TbUser;
import cn.e3mall.manager.po.ext.TbItemExt;
import cn.e3mall.sso.service.SsoService;

public class LoginInterceptor implements HandlerInterceptor {

	@Value("${TT_TOKEN}")
	private String TT_TOKEN;
	@Value("${COOKIE_CART_NAME}")
	private String COOKIE_CART_NAME;

	@Autowired
	private SsoService ssoService;
	@Autowired
	private CartService cartService;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 获取登录用户token值
		String token = CookieUtils.getCookieValue(request, TT_TOKEN, true);
		// 如果为空,说明没有登录,则放行(将购物车添加到cookie中)
		if (StringUtils.isEmpty(token)) {
			return true;
		}
		// 如果不为空,则调用sso服务,查询登录用户信息
		E3Result result = ssoService.checkLogin(token);
		// 如果登录用户为空,则说明登录超时,需要放行
		if (result.getData() == null) {
			return true;
		}
		// 登录成功,需要将用户信息写入request域中,key为user,value为user对象
		TbUser user = (TbUser) result.getData();
		request.setAttribute("user", user);
		// 登录成功,需要在第一次拦截时,合并cookie购物车和redis购物车(后补)
		String cookieValue = CookieUtils.getCookieValue(request, COOKIE_CART_NAME, true);
		List<TbItemExt> cookieCartList = new ArrayList<>();
		if(StringUtils.isNotEmpty(cookieValue)){
			cookieCartList = JsonUtils.jsonToList(cookieValue, TbItemExt.class);
			cartService.mergeCart(user.getId(),cookieCartList);
			// 合并购物车成功之后,清空cookie中的购物车(后补)
			CookieUtils.deleteCookie(request, response, COOKIE_CART_NAME);
		}
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
