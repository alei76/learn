package com.lin.service;

import java.util.List;

import com.lin.model.ContentObject;
/**
 *  Class Name: ContentService.java
 *  Function:
 *  @author JLC  From liutime.com
 *  @version 1.0
 */
public interface ContentService {
	/**
	 *  Function:
	 *  @author JLC 
	 *  @return
	 *  @throws Exception
	 */
	public List<ContentObject> getContentList() throws Exception;
}
