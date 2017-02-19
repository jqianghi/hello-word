package cn.e3mall.cms.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.po.E3Result;
import cn.e3mall.common.po.TreeNodeResult;
import cn.e3mall.manager.mapper.TbContentCategoryMapper;
import cn.e3mall.manager.po.TbContentCategory;
import cn.e3mall.manager.po.TbContentCategoryExample;
import cn.e3mall.manager.po.TbContentCategoryExample.Criteria;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	private TbContentCategoryMapper mapper;

	@Override
	public List<TreeNodeResult> queryContentCategoryList(Long parentId) {
		if (parentId == null) {
			return null;
		}
		// 根据父节点ID,查询子节点列表
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbContentCategory> list = mapper.selectByExample(example);

		// 封装返回结果
		List<TreeNodeResult> results = new ArrayList<>();
		TreeNodeResult result;
		for (TbContentCategory tbContentCategory : list) {
			result = new TreeNodeResult();
			result.setId(tbContentCategory.getId());
			result.setText(tbContentCategory.getName());

			// 父节点为closed,否则为open
			result.setState(tbContentCategory.getIsParent() ? "closed" : "open");

			results.add(result);
		}
		return results;
	}

	@Override
	public E3Result save(Long parentId, String name) {
		// 补全属性
		TbContentCategory category = new TbContentCategory();
		category.setParentId(parentId);
		category.setName(name);
		category.setIsParent(false);
		category.setStatus(1);
		category.setCreated(new Date());
		category.setUpdated(new Date());

		// 执行添加操作
		mapper.insert(category);

		// 修改父节点的isparent值为true.
		TbContentCategory parentCategory = new TbContentCategory();
		parentCategory.setId(parentId);
		parentCategory.setIsParent(true);
		mapper.updateByPrimaryKeySelective(parentCategory);
		
		// 返回结果对象,并将category的id返回
		return E3Result.ok(category);
	}

}
