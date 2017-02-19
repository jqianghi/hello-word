package test;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSolrCloud {

	@Test
	public void test() throws Exception {
		String zkHost = "192.168.242.139:2281,192.168.242.139:2282,192.168.242.139:2283";
		// 创建CloudSolrServer
		CloudSolrServer solrServer = new CloudSolrServer(zkHost);
		// 设置默认的collection
		solrServer.setDefaultCollection("collection2");
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", "test001");
		doc.addField("item_title", "solrcloud 测试商品");
		// 调用CloudSolrServer的api方法实现索引操作
		solrServer.add(doc);
		solrServer.commit();
	}

	@Test
	public void testSpring4CloudSolrServer() throws Exception {
		// 获取CloudSolrServer实例
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-solr.xml");
		// CloudSolrServer solrServer = context.getBean(CloudSolrServer.class);
		SolrServer solrServer = context.getBean(SolrServer.class);
		
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", "test002");
		doc.addField("item_title", "solrcloud 测试商品2");
		// 调用CloudSolrServer的api方法实现索引操作
		solrServer.add(doc);
		solrServer.commit();
	}

}
