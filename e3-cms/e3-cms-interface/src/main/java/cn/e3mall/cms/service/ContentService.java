package cn.e3mall.cms.service;

import java.util.List;

import cn.e3mall.common.po.E3Result;
import cn.e3mall.manager.po.TbContent;

public interface ContentService {

	public E3Result save(TbContent content);
	
	public List<TbContent> queryContentListByCategoryId(Long categoryId);
}
