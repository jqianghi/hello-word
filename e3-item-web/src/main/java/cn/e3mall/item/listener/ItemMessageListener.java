package cn.e3mall.item.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import cn.e3mall.manager.po.TbItem;
import cn.e3mall.manager.po.ext.TbItemExt;
import cn.e3mall.manager.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class ItemMessageListener implements MessageListener {

	@Autowired
	private ItemService itemService;

	@Autowired
	private FreeMarkerConfigurer configurer;

	@Value("${ITEM_TEMPLATE_NAME}")
	private String ITEM_TEMPLATE_NAME;

	@Value("${ITEM_HTML_PATH_PRE}")
	private String ITEM_HTML_PATH_PRE;

	@Value("${ITEM_HTML_PATH_EXT}")
	private String ITEM_HTML_PATH_EXT;

	@Override
	public void onMessage(Message message) {
		try {
			// 接收商品ID信息
			Long itemId = 0L;
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				if (textMessage != null) {
					itemId = Long.parseLong(textMessage.getText());
				}
			}
			// 根据商品ID,查询商品信息(相当于创建出freemarker的数据模型)
			TbItem item = itemService.queryItemById(itemId);
			// 对象复制
			TbItemExt itemExt = new TbItemExt();
			if (item != null) {
				BeanUtils.copyProperties(item, itemExt);
			}

			Map<String, Object> map = new HashMap<>();
			map.put("item", itemExt);
			// 获取商品详情页模板(ftl)
			Configuration configuration = configurer.getConfiguration();
			Template template = configuration.getTemplate(ITEM_TEMPLATE_NAME);
			// 指定详情页输出路径
			String htmlPath = ITEM_HTML_PATH_PRE + itemId + ITEM_HTML_PATH_EXT;
			Writer writer = new FileWriter(new File(htmlPath));
			// 使用freemarker的api方法生成商品详情页
			template.process(map, writer);
			// 释放资源
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
