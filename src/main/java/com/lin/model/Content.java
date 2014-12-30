package com.lin.model;

import java.util.Date;

import com.lin.model.base.BaseModel;

/**
 * 
 * @author hackcoder
 *
 */
public class Content extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9165845707595907325L;
	/**
	 * 内容标题
	 */
	private String title;
	/**
	 * 内容详细信息
	 */
	private String content;
	/**
	 * 内容创建时间
	 */
	private Date createDate = new Date();

	public Content() {
		super();
	}

	public Content(String title, String content, Date createDate) {
		super();
		this.title = title;
		this.content = content;
		this.createDate = createDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
