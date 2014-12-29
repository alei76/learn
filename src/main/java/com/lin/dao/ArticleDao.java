package com.lin.dao;

import java.util.List;

import com.lin.model.ContentObject;

public interface ArticleDao {
	/**
	 * Function:取得所有文章内容
	 * 
	 * @author JLC
	 * @return
	 */
	public List<ContentObject> getContentList();
}
