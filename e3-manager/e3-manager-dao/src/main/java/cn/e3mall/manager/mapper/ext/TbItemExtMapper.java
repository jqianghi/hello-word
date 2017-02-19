package cn.e3mall.manager.mapper.ext;

import java.util.List;

import cn.e3mall.manager.po.ext.TbItemExt;

public interface TbItemExtMapper {
	List<TbItemExt> queryItemAll();
	
	TbItemExt queryItemById(Long itemId);
}