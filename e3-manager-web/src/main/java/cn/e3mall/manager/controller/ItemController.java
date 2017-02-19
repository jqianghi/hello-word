package cn.e3mall.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.po.DatagridResult;
import cn.e3mall.common.po.E3Result;
import cn.e3mall.manager.po.TbItem;
import cn.e3mall.manager.service.ItemService;

@RequestMapping("/item")
@Controller
public class ItemController {

	@Autowired
	private ItemService service;

	@RequestMapping("/test/{id}")
	@ResponseBody
	public TbItem queryItemById(@PathVariable Long id) {
		return service.queryItemById(id);
	}

	@RequestMapping("/list")
	@ResponseBody
	public DatagridResult list(@RequestParam(defaultValue = "1") Integer page, Integer rows) {
		return service.queryItemList(page, rows);
	}
	
	@RequestMapping("/save")
	@ResponseBody
	public E3Result save(TbItem item){
		return service.save(item);
	}
}
