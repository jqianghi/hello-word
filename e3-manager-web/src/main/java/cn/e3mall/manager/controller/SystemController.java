package cn.e3mall.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SystemController {

	@RequestMapping("/")
	public String index(){
		return "index";
	}
	
	@RequestMapping("/system/{page}")
	public String showPage(@PathVariable String page){
		return page;
	}
}
