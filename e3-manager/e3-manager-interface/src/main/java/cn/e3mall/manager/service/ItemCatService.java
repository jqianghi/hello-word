package cn.e3mall.manager.service;

import java.util.List;

import cn.e3mall.common.po.TreeNodeResult;

public interface ItemCatService {

	public List<TreeNodeResult> queryItemCatList(Long parentId);
}
