package cn.e3mall.cart.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.common.po.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.manager.mapper.TbItemMapper;
import cn.e3mall.manager.po.TbItem;
import cn.e3mall.manager.po.ext.TbItemExt;
import redis.clients.jedis.JedisCluster;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private JedisCluster cluster;

	@Value("${REDIS_CART_KEY_PRE}")
	private String REDIS_CART_KEY_PRE;

	@Autowired
	private TbItemMapper mapper;

	@Override
	public List<TbItemExt> getCartListFromRedis(Long userId) {
		String cartValue = cluster.get(REDIS_CART_KEY_PRE + userId);
		List<TbItemExt> cartList = new ArrayList<>();
		if (StringUtils.isNotEmpty(cartValue)) {
			cartList = JsonUtils.jsonToList(cartValue, TbItemExt.class);
		}
		//wangji

		return cartList;
	}

	@Override
	public E3Result addCart(Long userId, Long itemId, Integer num) {
		// 获取redis中的购物车集合
		List<TbItemExt> cartList = getCartListFromRedis(userId);

		// 是否包含商品的标志
		boolean flag = false;

		// 遍历购物车集合
		for (TbItemExt tbItemExt : cartList) {
			// 如果购物车中有该商品,则数量追加
			if (itemId.equals(tbItemExt.getId())) {
				tbItemExt.setNum(tbItemExt.getNum() + num);
				flag = true;
				break;
			}
		}
		// 如果没有
		if (!flag) {
			// 根据商品ID查询数据库
			TbItem item = mapper.selectByPrimaryKey(itemId);
			// 对象复制
			TbItemExt itemExt = new TbItemExt();
			if (item != null) {
				BeanUtils.copyProperties(item, itemExt);
			}
			// 设置购物车商品购买数量
			itemExt.setNum(num);
			// 将查询结果添加到购物车中
			cartList.add(itemExt);
		}
		// 将修改后的购物车更新到redis中
		cluster.set(REDIS_CART_KEY_PRE + userId, JsonUtils.objectToJson(cartList));
		// 设置购物车有效期
		cluster.expire(REDIS_CART_KEY_PRE + userId, 86400 * 7);
		return E3Result.ok();
	}

	@Override
	public E3Result mergeCart(Long userId, List<TbItemExt> cookieCartList) {
		for (TbItemExt tbItemExt : cookieCartList) {
			addCart(userId, tbItemExt.getId(), tbItemExt.getNum());
		}
		return E3Result.ok();
	}

	@Override
	public E3Result updateCartItemNum(Long userId, Long itemId, Integer num) {
		// 获取redis中的购物车
		List<TbItemExt> cartList = getCartListFromRedis(userId);
		// 遍历购物车集合,修改指定商品购买数量
		for (TbItemExt tbItemExt : cartList) {
			if (itemId.equals(tbItemExt.getId())) {
				tbItemExt.setNum(num);
				break;
			}
		}
		// 将修改之后的购物车集合更新到redis中
		cluster.set(REDIS_CART_KEY_PRE + userId, JsonUtils.objectToJson(cartList));
		// 设置购物车有效期
		cluster.expire(REDIS_CART_KEY_PRE + userId, 86400 * 7);
		return E3Result.ok();
	}

	@Override
	public E3Result deleteCartItem(Long userId, Long itemId) {
		// 获取redis中的购物车
		List<TbItemExt> cartList = getCartListFromRedis(userId);
		// 遍历购物车集合,删除指定商品
		for (TbItemExt tbItemExt : cartList) {
			if (itemId.equals(tbItemExt.getId())) {
				cartList.remove(tbItemExt);
				break;
			}
		}
		// 将修改之后的购物车集合更新到redis中
		cluster.set(REDIS_CART_KEY_PRE + userId, JsonUtils.objectToJson(cartList));
		// 设置购物车有效期
		cluster.expire(REDIS_CART_KEY_PRE + userId, 86400 * 7);
		return E3Result.ok();
	}

}
