package cn.e3mall.manager.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.po.TreeNodeResult;
import cn.e3mall.manager.mapper.TbItemCatMapper;
import cn.e3mall.manager.po.TbItemCat;
import cn.e3mall.manager.po.TbItemCatExample;
import cn.e3mall.manager.po.TbItemCatExample.Criteria;

@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper mapper;

	@Override
	public List<TreeNodeResult> queryItemCatList(Long parentId) {
		if (parentId == null) {
			return null;
		}
		//根据父节点ID,查询子节点列表
		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbItemCat> list = mapper.selectByExample(example);
		
		//将查询结果封装到List<TreeNodeResult>
		List<TreeNodeResult> results = new ArrayList<>();
		TreeNodeResult result;
		for (TbItemCat tbItemCat : list) {
			result = new TreeNodeResult();
			result.setId(tbItemCat.getId());
			result.setText(tbItemCat.getName());
			//如果是父节点,则状态为closed,否则为open
			result.setState(tbItemCat.getIsParent()?"closed":"open");
			
			results.add(result);
		}
		return results;
	}

}
