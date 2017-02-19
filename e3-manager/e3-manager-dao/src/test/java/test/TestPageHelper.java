package test;

import java.util.List;

import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.reflection.wrapper.BaseWrapper;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.manager.mapper.TbItemMapper;
import cn.e3mall.manager.po.TbItem;
import cn.e3mall.manager.po.TbItemExample;

public class TestPageHelper {

	@Test
	public void test() {
		// 初始化分页信息
		PageHelper.startPage(1, 30);

		// 执行mapper方法
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");
		TbItemMapper mapper = context.getBean(TbItemMapper.class);

		TbItemExample example = new TbItemExample();
		List<TbItem> list = mapper.selectByExample(example);

		// 封装PageInfo对象，获取分页信息
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);

		//输出分页信息
		System.out.println("总页数："+pageInfo.getPages());
		System.out.println("总记录数："+pageInfo.getTotal());
		
	}
}
