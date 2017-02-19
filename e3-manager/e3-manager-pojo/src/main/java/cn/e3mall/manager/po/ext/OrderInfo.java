package cn.e3mall.manager.po.ext;

import java.util.List;

import cn.e3mall.manager.po.TbOrder;
import cn.e3mall.manager.po.TbOrderItem;
import cn.e3mall.manager.po.TbOrderShipping;

public class OrderInfo extends TbOrder {

	//订单商品明细集合
	private List<TbOrderItem> orderItems;
	
	//订单配送信息
	private TbOrderShipping orderShipping;

	public List<TbOrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<TbOrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	public TbOrderShipping getOrderShipping() {
		return orderShipping;
	}

	public void setOrderShipping(TbOrderShipping orderShipping) {
		this.orderShipping = orderShipping;
	}
	
	
	
}
