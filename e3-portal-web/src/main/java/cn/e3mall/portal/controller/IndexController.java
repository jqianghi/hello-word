package cn.e3mall.portal.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.cms.service.ContentService;
import cn.e3mall.common.po.AdResult;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.manager.po.TbContent;

@Controller
public class IndexController {

	@Autowired
	private ContentService service;

	@Value("${AD1_CATEGORY_ID}")
	private Long AD1_CATEGORY_ID;

	@Value("${AD1_HEIGHT}")
	private Integer AD1_HEIGHT;

	@Value("${AD1_HEIGHTB}")
	private Integer AD1_HEIGHTB;

	@Value("${AD1_WIDTH}")
	private Integer AD1_WIDTH;

	@Value("${AD1_WIDTHB}")
	private Integer AD1_WIDTHB;

	@RequestMapping("/")
	public String index(Model model) {

		// 根据广告位ID,查询广告列表
		List<TbContent> list = service.queryContentListByCategoryId(AD1_CATEGORY_ID);

		// 将查询结果封装到List<AdResult>集合中
		List<AdResult> results = new ArrayList<>();
		AdResult result;
		for (TbContent tbContent : list) {
			result = new AdResult();
			// 将广告信息封装
			result.setAlt(tbContent.getTitle());
			result.setHref(tbContent.getUrl());
			result.setSrc(tbContent.getPic());
			result.setSrcB(tbContent.getPic2());

			// 封装页面信息
			result.setHeight(AD1_HEIGHT);
			result.setHeightB(AD1_HEIGHTB);
			result.setWidth(AD1_WIDTH);
			result.setWidthB(AD1_WIDTHB);
			results.add(result);
		}

		// 将List<AdResult>放入request中,key为ad1
		model.addAttribute("ad1", JsonUtils.objectToJson(results));
		return "index";
	}
}
