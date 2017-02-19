package cn.e3mall.search.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.po.E3Result;
import cn.e3mall.manager.mapper.ext.TbItemExtMapper;
import cn.e3mall.manager.po.TbItem;
import cn.e3mall.manager.po.ext.TbItemExt;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	private TbItemExtMapper extMapper;

	@Autowired
	private SolrServer solrServer;

	@Override
	public E3Result importAll() {
		// 从数据库中查询所有的商品数据
		List<TbItemExt> list = extMapper.queryItemAll();
		// 调用solrServer的api方法实现索引的导入
		List<SolrInputDocument> docs = new ArrayList<>();
		SolrInputDocument document;
		for (TbItemExt tbItemExt : list) {
			document = new SolrInputDocument();
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

			docs.add(document);
		}

		try {
			solrServer.add(docs);
			// 提交
			solrServer.commit();
		} catch (Exception e) {
			e.printStackTrace();
			return E3Result.build(500, "你中病毒了!");
		}
		// 返回导入成功失败状态
		return E3Result.ok();
	}

	@Override
	public Map<String, Object> search(String q, Integer page) throws Exception {
		// 创建SolrQuery对象
		SolrQuery query = new SolrQuery();

		// 设置关键字查询条件
		if (StringUtils.isNotEmpty(q)) {
			query.setQuery(q);
		} else {
			query.setQuery("*:*");
		}
		// 设置默认域
		query.set("df", "item_keywords");

		// 设置分页条件
		if (page == null) {
			page = 1;
		}
		query.setStart((page - 1) * 20);
		query.setRows(20);

		// 设置高亮
		query.setHighlight(true);
		query.addHighlightField("item_title");
		query.setHighlightSimplePre("<font style='color:red'>");
		query.setHighlightSimplePost("</font>");

		// 执行搜索
		QueryResponse response = solrServer.query(query);

		// 获取搜索结果
		SolrDocumentList results = response.getResults();
		// 获取总记录数
		long totalRecords = results.getNumFound();

		// 封装List<TbItem>
		List<TbItem> itemList = new ArrayList<>();
		TbItem item;

		// 获取高亮信息
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
		for (SolrDocument solrDocument : results) {
			item = new TbItem();
			item.setId(Long.parseLong(solrDocument.get("id").toString()));
			// 商品名称高亮处理
			List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
			if (list != null && list.size() > 0) {
				item.setTitle(list.get(0));
			} else {
				item.setTitle(solrDocument.get("item_title").toString());
			}

			item.setPrice(Long.parseLong(solrDocument.get("item_price").toString()));
			item.setImage(solrDocument.get("item_image").toString());

			itemList.add(item);
		}

		// 计算总页数
		int totalPages = (int) (totalRecords / 20);
		if (totalRecords % 20 > 0) {
			totalPages++;
		}

		// 封装返回结果
		Map<String, Object> map = new HashMap<>();
		map.put("totalPages", totalPages);
		map.put("totalRecords", totalRecords);
		map.put("itemList", itemList);
		return map;
	}
}
