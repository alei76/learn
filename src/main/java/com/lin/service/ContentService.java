package com.lin.service;

import java.util.List;

import com.lin.model.Content;

/**
 * 
 * @author hackcoder
 *
 */
public interface ContentService {
	/**
	 * Function:
	 * 
	 * @author JLC
	 * @return
	 * @throws Exception
	 */
	public List<Content> getContentList() throws Exception;
}
