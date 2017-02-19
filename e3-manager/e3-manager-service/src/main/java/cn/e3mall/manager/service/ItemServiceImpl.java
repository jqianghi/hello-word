package cn.e3mall.manager.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.po.DatagridResult;
import cn.e3mall.common.po.E3Result;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.manager.mapper.TbItemMapper;
import cn.e3mall.manager.po.TbItem;
import cn.e3mall.manager.po.TbItemExample;
import redis.clients.jedis.JedisCluster;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper mapper;

	@Autowired
	private JedisCluster cluster;

	@Value("${REDIS_ITEM_KEY_PRE}")
	private String REDIS_ITEM_KEY_PRE;
	
	@Value("${REDIS_ITEM_EXPIRE}")
	private int REDIS_ITEM_EXPIRE;
	
	
	
	@Override
	public TbItem queryItemById(Long id) {
		if (id == null) {
			return null;
		}

		// 查询缓存中是否有该商品
		String itemJson = "";
		try {
			itemJson = cluster.get(REDIS_ITEM_KEY_PRE + id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 如果有数据,则直接返回
		if (StringUtils.isNotEmpty(itemJson)) {
			return JsonUtils.jsonToPojo(itemJson, TbItem.class);
		}

		TbItem item = mapper.selectByPrimaryKey(id);

		try {
			// 将查询到的商品添加到缓存中
			cluster.set(REDIS_ITEM_KEY_PRE + id, JsonUtils.objectToJson(item));
			// 设置有效期
			cluster.expire(REDIS_ITEM_KEY_PRE + id, REDIS_ITEM_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return item;
	}

	@Override
	public DatagridResult queryItemList(Integer page, Integer rows) {
		if (page == null) {
			page = 1;
		}
		if (rows == null) {
			rows = 30;
		}

		// 初始化分页信息
		PageHelper.startPage(page, rows);

		// 执行查询方法
		TbItemExample example = new TbItemExample();
		List<TbItem> list = mapper.selectByExample(example);

		// 封装PageInfo对象
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);

		// 封装返回对象DatagridResult
		DatagridResult result = new DatagridResult();
		result.setTotal(pageInfo.getTotal());
		result.setRows(list);
		return result;
	}

	@Autowired
	private JmsTemplate template;

	@Resource(name = "itemTopic")
	private Destination destination;

	@Override
	public E3Result save(TbItem item) {
		// 补全属性
		// 商品ID
		final long itemId = IDUtils.genItemId();
		item.setId(itemId);
		// 商品状态
		item.setStatus((byte) 1);
		// 商品创建时间\更新时间
		item.setCreated(new Date());
		item.setUpdated(new Date());

		mapper.insert(item);

		try {
			// 索引库同步
			template.send(destination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					// 发送商品ID信息
					return session.createTextMessage(itemId + "");
				}
			});
		} catch (JmsException e) {
			e.printStackTrace();
		}
		return E3Result.ok();
	}
}
