package cn.e3mall.sso.service;

import cn.e3mall.common.po.E3Result;
import cn.e3mall.manager.po.TbUser;

public interface SsoService {

	public E3Result register(TbUser user);
	
	public E3Result login(String username,String password);
	
	public E3Result checkLogin(String token);
}
