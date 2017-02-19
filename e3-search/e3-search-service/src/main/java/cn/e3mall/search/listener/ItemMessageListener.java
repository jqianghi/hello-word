package cn.e3mall.search.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import cn.e3mall.manager.mapper.ext.TbItemExtMapper;
import cn.e3mall.manager.po.ext.TbItemExt;

public class ItemMessageListener implements MessageListener {

	@Autowired
	private TbItemExtMapper extMapper;

	@Autowired
	private SolrServer solrServer;

	@Override
	public void onMessage(Message message) {
		try {
			// 接收商品ID信息
			long itemId = 0L;
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				if (textMessage != null) {
					itemId = Long.parseLong(textMessage.getText());
				}
			}

			// 根据商品ID,去数据库中查询商品数据
			TbItemExt tbItemExt = extMapper.queryItemById(itemId);

			// 将查询结果转成SolrInputDocument对象
			SolrInputDocument document = new SolrInputDocument();
			document.addField("id", tbItemExt.getId());
			document.addField("item_title", tbItemExt.getTitle());
			document.addField("item_sell_point", tbItemExt.getSellPoint());
			document.addField("item_category_name", tbItemExt.getCatName());
			document.addField("item_price", tbItemExt.getPrice());
			document.addField("item_description", tbItemExt.getDescription());

			// 商品图片需要特殊处理(数据库中image字段存储了多张图片的地址,而页面中只需要一张图片地址)
			if (tbItemExt.getImage() != null) {
				String[] imgArr = tbItemExt.getImage().split(",");
				if (imgArr != null && imgArr.length > 0) {
					document.addField("item_image", imgArr[0]);
				}
			}

			// 执行索引导入
			solrServer.add(document);
			solrServer.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
