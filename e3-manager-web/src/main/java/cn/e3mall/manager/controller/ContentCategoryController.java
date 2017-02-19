package cn.e3mall.manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.cms.service.ContentCategoryService;
import cn.e3mall.common.po.E3Result;
import cn.e3mall.common.po.TreeNodeResult;

@RequestMapping("/contentCategory")
@Controller
public class ContentCategoryController {

	@Autowired
	private ContentCategoryService service;

	@RequestMapping("/list")
	@ResponseBody
	public List<TreeNodeResult> list(@RequestParam(value = "id", defaultValue = "0") Long parentId) {
		return service.queryContentCategoryList(parentId);
	}

	@RequestMapping("/create")
	@ResponseBody
	public E3Result create(Long parentId, String name) {
		return service.save(parentId, name);
	}
}
