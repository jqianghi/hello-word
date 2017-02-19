package cn.e3mall.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.cms.service.ContentService;
import cn.e3mall.common.po.E3Result;
import cn.e3mall.manager.po.TbContent;

@RequestMapping("/content")
@Controller
public class ContentController {

	@Autowired
	private ContentService service;

	@RequestMapping("/save")
	@ResponseBody
	public E3Result save(TbContent content) {
		return service.save(content);
	}
}
