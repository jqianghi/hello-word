package cn.e3mall.order.service;

import cn.e3mall.common.po.E3Result;
import cn.e3mall.manager.po.ext.OrderInfo;

public interface OrderService {

	public E3Result saveOrder(OrderInfo orderInfo);
}
