package cn.e3mall.manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.po.TreeNodeResult;
import cn.e3mall.manager.service.ItemCatService;

@RequestMapping("/itemCat")
@Controller
public class ItemCatController {

	@Autowired
	private ItemCatService service;

	@RequestMapping("/list")
	@ResponseBody
	public List<TreeNodeResult> list(@RequestParam(value = "id", defaultValue = "0") Long parentId) {
		return service.queryItemCatList(parentId);
	}
}
