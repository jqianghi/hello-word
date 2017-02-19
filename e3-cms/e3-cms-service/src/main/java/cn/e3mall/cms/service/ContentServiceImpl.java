package cn.e3mall.cms.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.common.po.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.manager.mapper.TbContentMapper;
import cn.e3mall.manager.po.TbContent;
import cn.e3mall.manager.po.TbContentExample;
import cn.e3mall.manager.po.TbContentExample.Criteria;
import redis.clients.jedis.JedisCluster;

@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper mapper;

	@Override
	public E3Result save(TbContent content) {
		// 补全日期属性
		content.setCreated(new Date());
		content.setUpdated(new Date());
		mapper.insert(content);
		return E3Result.ok();
	}

	@Autowired
	private JedisCluster cluster;

	@Value("${REDIS_CONTENT_KEY}")
	private String REDIS_CONTENT_KEY;

	@Override
	public List<TbContent> queryContentListByCategoryId(Long categoryId) {
		if (categoryId == null) {
			return null;
		}

		// 先查询缓存
		String json;
		try {
			json = cluster.hget(REDIS_CONTENT_KEY, categoryId + "");
			// 如果有数据,则直接返回
			if (StringUtils.isNotEmpty(json)) {
				return JsonUtils.jsonToList(json, TbContent.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 如果没有数据,则查询数据库,如何保存到缓存中
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);

		List<TbContent> list = mapper.selectByExample(example);

		try {
			cluster.hset(REDIS_CONTENT_KEY, categoryId + "", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

}
