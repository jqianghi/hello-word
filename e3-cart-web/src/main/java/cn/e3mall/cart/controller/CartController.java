package cn.e3mall.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.cart.utils.CookieUtils;
import cn.e3mall.common.po.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.manager.po.TbItem;
import cn.e3mall.manager.po.TbUser;
import cn.e3mall.manager.po.ext.TbItemExt;
import cn.e3mall.manager.service.ItemService;

@RequestMapping("/cart")
@Controller
public class CartController {

	@Value("${COOKIE_CART_NAME}")
	private String COOKIE_CART_NAME;

	@Autowired
	private CartService cartService;

	@RequestMapping("/showCart")
	public String showCart(HttpServletRequest request) {
		TbUser user = (TbUser) request.getAttribute("user");
		// 如果用户不为空,则从redis中获取购物车
		List<TbItemExt> cartList = new ArrayList<>();
		if (user != null) {
			cartList = cartService.getCartListFromRedis(user.getId());
		} else {
			cartList = getCartListFromCookie(request);
		}

		// 将购物车放到request中
		request.setAttribute("cartList", cartList);
		return "cart";
	}

	private List<TbItemExt> getCartListFromCookie(HttpServletRequest request) {
		// 从cookie中取出购物车集合
		String cookieValue = CookieUtils.getCookieValue(request, COOKIE_CART_NAME, true);
		List<TbItemExt> cartList = new ArrayList<>();
		if (StringUtils.isNotEmpty(cookieValue)) {
			cartList = JsonUtils.jsonToList(cookieValue, TbItemExt.class);
		}
		return cartList;
	}

	@Autowired
	private ItemService itemService;

	@RequestMapping("/add/{itemId}/{num}")
	public String addCart(@PathVariable Long itemId, @PathVariable Integer num, HttpServletRequest request,
			HttpServletResponse response) {

		TbUser user = (TbUser) request.getAttribute("user");
		// 如果用户不为空,则去redis中添加购物车
		if (user != null) {
			E3Result result = cartService.addCart(user.getId(), itemId, num);
			if (result.getStatus() == 200) {
				return "cartSuccess";
			}
		}

		// 从cookie中取出购物车集合
		List<TbItemExt> cartList = getCartListFromCookie(request);

		// 购物车中是否包含该商品的标示
		boolean flag = false;
		for (TbItemExt tbItemExt : cartList) {
			// Long类型数值的判断是否相等,需要使用equals进行判断
			if (itemId.equals(tbItemExt.getId())) {
				// 如果购物车中有该商品,数量追加
				tbItemExt.setNum(tbItemExt.getNum() + num);
				flag = true;
				break;
			}
		}
		// 如果购物车中没有该商品
		if (!flag) {
			// 查询数据库
			TbItem item = itemService.queryItemById(itemId);

			// 对象复制
			TbItemExt itemExt = new TbItemExt();
			if (item != null) {
				BeanUtils.copyProperties(item, itemExt);
			}

			// 设置商品购买数量为num
			itemExt.setNum(num);
			// 将查询结果添加到购物车中
			cartList.add(itemExt);
		}
		// 将修改后的购物车写入cookie
		CookieUtils.setCookie(request, response, COOKIE_CART_NAME, JsonUtils.objectToJson(cartList), true);
		return "cartSuccess";
	}

	@RequestMapping("/update/num/{itemId}/{num}")
	@ResponseBody
	public E3Result updateCartItemNum(@PathVariable Long itemId, @PathVariable Integer num, HttpServletRequest request,
			HttpServletResponse response) {
		
		TbUser user = (TbUser) request.getAttribute("user");
		// 如果用户不为空,则去redis中添加购物车
		if (user != null) {
			cartService.updateCartItemNum(user.getId(), itemId, num);
			return E3Result.ok();
		}
		
		// 从cookie中取出购物车集合
		List<TbItemExt> cartList = getCartListFromCookie(request);

		// 修改指定商品的数量
		for (TbItemExt tbItemExt : cartList) {
			if (itemId.equals(tbItemExt.getId())) {
				tbItemExt.setNum(num);
				break;
			}
		}

		// 将修改后的购物车写入cookie
		CookieUtils.setCookie(request, response, COOKIE_CART_NAME, JsonUtils.objectToJson(cartList), true);
		return E3Result.ok();
	}

	@RequestMapping("/delete/{itemId}")
	public String deleteCartItem(@PathVariable Long itemId, HttpServletRequest request, HttpServletResponse response) {
		TbUser user = (TbUser) request.getAttribute("user");
		// 如果用户不为空,则去redis中添加购物车
		if (user != null) {
			cartService.deleteCartItem(user.getId(), itemId);
			return "redirect:/cart/showCart";
		}
		
		// 从cookie中取出购物车集合
		List<TbItemExt> cartList = getCartListFromCookie(request);

		// 删除指定商品
		for (TbItemExt tbItemExt : cartList) {
			if (itemId.equals(tbItemExt.getId())) {
				cartList.remove(tbItemExt);
				break;
			}
		}

		// 将修改后的购物车写入cookie
		CookieUtils.setCookie(request, response, COOKIE_CART_NAME, JsonUtils.objectToJson(cartList), true);

		return "redirect:/cart/showCart";
	}
}
