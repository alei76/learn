package com.lin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lin.dao.ArticleDao;
import com.lin.model.ContentObject;
import com.lin.service.ContentService;

@Service("contentService")
public class ContentServiceImpl implements ContentService {

	@Autowired
	private ArticleDao articleDao;

	public void setArticleDao(ArticleDao articleDao) {
		this.articleDao = articleDao;
	}

	public List<ContentObject> getContentList() throws Exception {
		return articleDao.getContentList();
	}

}
