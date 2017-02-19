package cn.e3mall.item.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.manager.po.TbItem;
import cn.e3mall.manager.po.ext.TbItemExt;
import cn.e3mall.manager.service.ItemService;

@RequestMapping("/item")
@Controller
public class ItemController {

	@Autowired
	private ItemService service;

	@RequestMapping("/{itemId}")
	public String showDetail(@PathVariable Long itemId, Model model) {
		// 根据商品ID查询商品数据
		TbItem item = service.queryItemById(itemId);

		// 将tbItem对象复制到TbItemExt对象中
		TbItemExt itemExt = new TbItemExt();
		if (item != null) {
			BeanUtils.copyProperties(item, itemExt);
		}

		// 将TbItemExt对象放入request域中,key为item
		model.addAttribute("item", itemExt);
		return "item";
	}
}
