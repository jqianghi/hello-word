package cn.e3mall.order.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.common.po.E3Result;
import cn.e3mall.manager.mapper.TbOrderItemMapper;
import cn.e3mall.manager.mapper.TbOrderMapper;
import cn.e3mall.manager.mapper.TbOrderShippingMapper;
import cn.e3mall.manager.po.TbOrderItem;
import cn.e3mall.manager.po.ext.OrderInfo;
import redis.clients.jedis.JedisCluster;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	@Autowired
	private TbOrderShippingMapper orderShippingMapper;

	@Autowired
	private JedisCluster cluster;

	@Value("${REDIS_ORDER_ID_KEY}")
	private String REDIS_ORDER_ID_KEY;

	@Value("${REDIS_ORDER_ID_START_NUM}")
	private String REDIS_ORDER_ID_START_NUM;

	@Value("${REDIS_ORDER_ITEM_ID_KEY}")
	private String REDIS_ORDER_ITEM_ID_KEY;

	@Override
	public E3Result saveOrder(OrderInfo orderInfo) {
		// 添加订单表
		// 生成订单ID(使用redis的自增策略生成)
		String value = cluster.get(REDIS_ORDER_ID_KEY);
		if (StringUtils.isEmpty(value)) {
			// 如果是没有数值,则设置初始默认值
			cluster.set(REDIS_ORDER_ID_KEY, REDIS_ORDER_ID_START_NUM);
		}
		// 通过自增方法获取订单ID
		String orderId = cluster.incr(REDIS_ORDER_ID_KEY).toString();

		orderInfo.setOrderId(orderId);
		// 补全订单属性(status\postfee\created\ updated)
		orderInfo.setStatus(1);
		orderInfo.setPostFee("0");
		orderInfo.setCreateTime(new Date());
		orderInfo.setUpdateTime(new Date());

		orderMapper.insert(orderInfo);
		// 添加订单商品明细表
		for (TbOrderItem orderItem : orderInfo.getOrderItems()) {
			String orderItemId = cluster.incr(REDIS_ORDER_ITEM_ID_KEY).toString();
			orderItem.setId(orderItemId);
			orderItem.setOrderId(orderId);
			orderItemMapper.insert(orderItem);
		}
		// 添加订单配送表
		orderInfo.getOrderShipping().setOrderId(orderId);
		orderShippingMapper.insert(orderInfo.getOrderShipping());
		
		return E3Result.ok(orderId);
	}

}
