package cn.e3mall.search.controller;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.e3mall.search.service.SearchService;

@Controller
public class SearchController {

	@Autowired
	private SearchService service;

	@RequestMapping("/search")
	public String search(String q, @RequestParam(defaultValue = "1") Integer page, Model model) {
		try {
			// 解决get方式中文乱码
			if (StringUtils.isNotEmpty(q)) {
				q = new String(q.getBytes("iso8859-1"), "utf8");
			}

			// 调用service的搜索方法,查询搜索结果
			Map<String, Object> map = service.search(q, page);
			// 将数据放入request中
			// query（查询条件）、page（当前页）、totalPages（总页数）、itemList（搜索结果）、totalRecords（总记录数）
			model.addAttribute("query", q);
			model.addAttribute("page", page);
			model.addAttribute("totalPages", map.get("totalPages"));
			model.addAttribute("totalRecords", map.get("totalRecords"));
			model.addAttribute("itemList", map.get("itemList"));

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", "你中毒了");
			return "error/exception";
		}
		return "search";
	}
}
