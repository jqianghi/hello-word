package cn.e3mall.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.po.E3Result;
import cn.e3mall.manager.po.TbUser;
import cn.e3mall.manager.po.ext.OrderInfo;
import cn.e3mall.manager.po.ext.TbItemExt;
import cn.e3mall.order.service.OrderService;

@RequestMapping("/order")
@Controller
public class OrderController {

	@Autowired
	private CartService cartService;

	@RequestMapping("/order-cart")
	public String showOrderCart(HttpServletRequest request) {
		// 获取用户信息
		TbUser user = (TbUser) request.getAttribute("user");
		List<TbItemExt> cartList = cartService.getCartListFromRedis(user.getId());
		// 将购物车集合,放入request域中,key为cartList
		request.setAttribute("cartList", cartList);
		return "order-cart";
	}

	@Autowired
	private OrderService orderService;

	@RequestMapping("/create")
	public String saveOrder(OrderInfo orderInfo, HttpServletRequest request) {
		// 设置用户ID
		TbUser user = (TbUser) request.getAttribute("user");
		orderInfo.setUserId(user.getId());
		orderInfo.setBuyerNick(user.getUsername());
		E3Result result = orderService.saveOrder(orderInfo);
		// 需要在request域中设置orderId\payment\date
		if (result.getData() != null) {
			request.setAttribute("orderId", result.getData());
		}
		request.setAttribute("payment", orderInfo.getPayment());

		// 计算预计日期
		DateTime dateTime = new DateTime();
		dateTime = dateTime.plusDays(3);
		request.setAttribute("date", dateTime.toString("yyyy-MM-dd"));
		return "success";
	}
}
