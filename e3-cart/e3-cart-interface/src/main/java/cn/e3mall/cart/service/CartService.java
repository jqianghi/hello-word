package cn.e3mall.cart.service;

import java.util.List;

import cn.e3mall.common.po.E3Result;
import cn.e3mall.manager.po.ext.TbItemExt;

public interface CartService {

	public List<TbItemExt> getCartListFromRedis(Long userId);

	public E3Result addCart(Long userId, Long itemId, Integer num);
	
	public E3Result mergeCart(Long userId,List<TbItemExt> cookieCartList);
	
	public E3Result updateCartItemNum(Long userId,Long itemId,Integer num);
	
	public E3Result deleteCartItem(Long userId,Long itemId);
}
