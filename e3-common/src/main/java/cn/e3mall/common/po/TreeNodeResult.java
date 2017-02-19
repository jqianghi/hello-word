package cn.e3mall.common.po;

import java.io.Serializable;

public class TreeNodeResult implements Serializable {

	// 节点ID
	private Long id;
	// 节点文本
	private String text;
	// 节点状态，'open' 或 'closed'，默认：'open'。如果为'closed'的时候，将不自动展开该节点。
	private String state;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

	
	
}
