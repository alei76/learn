package com.lin.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.lin.action.base.BaseAction;
import com.lin.model.Content;
import com.lin.search.SearchEngineCore;
import com.lin.search.SearchObject;
import com.lin.service.ContentService;
import com.lin.util.ConfigConstant;
import com.lin.util.PackContentObject;

/**
 * 
 * @author hackcoder
 *
 */
@Controller
@Scope("prototype")
public class SearchAction extends BaseAction {

	private static final long serialVersionUID = 3141706082945311957L;

	@Resource(name = "contentServiceImpl")
	private ContentService contentService;

	@RequestMapping("/search")
	public ModelAndView searcheContent(@RequestParam("keyword") String keyword)
			throws Exception {
		List<Content> searcheResult = new ArrayList<Content>();
		ModelAndView modelAndView = new ModelAndView("list");
		// 当查询条件不存在时，查询数据库
		if (StringUtils.isEmpty(keyword)) {
			searcheResult = contentService.getContentList();
			modelAndView.addObject("searcheResult", searcheResult);
			return modelAndView;
		}
		// 取得查询对象
		IndexReader[] readers = SearchObject.getInstance().getSearcherReads();
		MultiReader mReaders = new MultiReader(readers);
		IndexSearcher indexSearch = new IndexSearcher(mReaders);
		Analyzer analyzer = new IKAnalyzer();
		// 创建boolean查询
		BooleanQuery query = new BooleanQuery();
		String[] field = { "title", "content" };
		BooleanClause.Occur[] flags = new BooleanClause.Occur[2];
		flags[0] = BooleanClause.Occur.SHOULD;
		flags[1] = BooleanClause.Occur.SHOULD;
		Query query1 = MultiFieldQueryParser.parse(ConfigConstant.version,
				QueryParser.escape(keyword), field, flags, analyzer);
		query.add(query1, Occur.MUST);
		TopScoreDocCollector topCollector = TopScoreDocCollector.create(10000,
				true);
		indexSearch.search(query, topCollector);
		// 取得查询结果
		TopDocs topDocs = topCollector.topDocs();
		int resultCount = topDocs.totalHits;
		for (int i = 0; i < resultCount; i++) {
			Document doc = indexSearch.doc(topDocs.scoreDocs[i].doc);
			// 转换Document对象为内容对象
			// Content content =
			// PackContent.convertDocToContent(doc);
			Content content = PackContentObject.convertDocToContent(doc,
					query1, analyzer);
			// 加入到结果列表 返回给前台页面获取
			searcheResult.add(content);
		}

		return modelAndView;
	}

	@RequestMapping("/add")
	public String addContent(@RequestParam("title") String title,
			@RequestParam("content") String content) throws Exception {
		SearchEngineCore se = SearchObject.getInstance().getLuceneContext(
				"search");
		Content contentObj = new Content();
		Document doc = PackContentObject.convertContentToDoc(contentObj);
		se.getTw().addDocument(doc);
		se.commitIndex();
		se.refreshData();
		return "success";
	}
}
