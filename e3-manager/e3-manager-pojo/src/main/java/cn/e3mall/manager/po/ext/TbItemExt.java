package cn.e3mall.manager.po.ext;

import cn.e3mall.manager.po.TbItem;

public class TbItemExt extends TbItem {

	// 添加商品分类名称
	private String catName;

	// 图片数组
	private String[] images;

	public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

	/**
	 * 数据库中的image字段是以逗号分隔的字符串,而详情页中需要图片数组,需要进行转换
	 * 
	 * @return
	 */
	public String[] getImages() {
		// 对image字段进行解析
		if (this.getImage() == null) {
			return new String[] { "www.baidu.com" };
		}
		return this.getImage().split(",");
	}

	public void setImages(String[] images) {
		this.images = images;
	}

}
