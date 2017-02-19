package cn.e3mall.cms.service;

import java.util.List;

import cn.e3mall.common.po.E3Result;
import cn.e3mall.common.po.TreeNodeResult;

public interface ContentCategoryService {

	public List<TreeNodeResult> queryContentCategoryList(Long parentId);
	
	public E3Result save(Long parentId,String name);
}
