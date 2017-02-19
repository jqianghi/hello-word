package test;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class TestFreemarker {

	@Test
	public void test() throws Exception {
		// 创建Configuration对象
		Configuration configuration = new Configuration(Configuration.getVersion());
		// 设置模板文件所在路径
		String tempDir = this.getClass().getResource("/ftl").getPath();
		tempDir = URLDecoder.decode(tempDir, "utf8");
		configuration.setDirectoryForTemplateLoading(new File(tempDir));
		// 设置模板文件默认编码
		configuration.setDefaultEncoding("utf8");

		// 获取指定模板
		Template template = configuration.getTemplate("hello.ftl");

		// 创建数据模型
		Map<String, Object> map = new HashMap<>();
		map.put("world", "freemarker");

		map.put("student", new Student(1, "zhangsan"));
		// 添加以日期类型
		map.put("today", new Date());

		// 添加一个List
		List<Student> stuList = new ArrayList<>();
		stuList.add(new Student(1, "张三1"));
		stuList.add(new Student(2, "张三2"));
		stuList.add(new Student(3, "张三3"));
		stuList.add(new Student(4, "张三4"));
		stuList.add(new Student(5, "张三5"));
		stuList.add(new Student(6, "张三6"));
		map.put("stulist", stuList);

		// 指定生产文件路径
		String outputFile = "D:\\freemarker\\out\\hm37\\hello.html";
		Writer writer = new FileWriter(new File(outputFile));

		// 通过Template的proce方法生成文件
		template.process(map, writer);

		// 释放资源
		writer.close();
	}

	@Test
	public void test02() throws Exception {
		// 获取Configuration对象
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:spring/applicationContext-freemarker.xml");
		FreeMarkerConfigurer configurer = context.getBean(FreeMarkerConfigurer.class);

		Configuration configuration = configurer.getConfiguration();

		// 获取指定模板
		Template template = configuration.getTemplate("hello2.ftl");

		// 创建数据模型
		Map<String, Object> map = new HashMap<>();
		map.put("world", "test spring and freemarker");

		// 指定文件输出目录
		String output = "D:\\freemarker\\out\\hm37\\hello2.html";
		Writer writer = new FileWriter(new File(output));

		// 调用Template的process方法,生成文件
		template.process(map, writer);

		// 释放资源
		writer.close();
	}

}
