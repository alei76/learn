package learn;

import java.util.ArrayList;
import java.util.List;

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

import com.lin.model.Content;
import com.lin.search.SearchEngineCore;
import com.lin.search.SearchObject;
import com.lin.util.PackContentObject;
import com.lin.util.WebContent;

/**
 * Class Name: mainTest.java
 * 
 * @author JLC From liutime.com
 * @version 1.0
 */
public class mainTest {
	/**
	 * Function:抓取网页内容
	 * 
	 * @author JLC DateTime 2013-4-19 上午03:30:48
	 * @return
	 */
	public static List<Document> getWebContentDocuments() {
		List<Document> docs = new ArrayList<Document>();
		WebContent wc = new WebContent();
		Document doc1 = PackContentObject.convertContentToDoc(wc
				.getContentFromSite("http://news.163.com"));
		docs.add(doc1);
		Document doc2 = PackContentObject.convertContentToDoc(wc
				.getContentFromSite("http://news.sohu.com/"));
		docs.add(doc2);
		return docs;
	}

	/**
	 * Function:创建搜索引擎数据
	 * 
	 * @author JLC
	 */
	public static void createSearchEngineData() {
		SearchEngineCore se = SearchObject.getInstance().getLuceneContext(
				"search");
		List<Document> docList = getWebContentDocuments();
		for (Document doc : docList) {
			try {
				Long gen = se.getTw().addDocument(doc);
				// se.commitIndex();
				se.refreshData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {
		// 创建数据
		// createSearchEngineData();
		// System.out.println();
		// searchContent("搜");
		// System.out.println("---");
		searchContent("搜狐");
		searchContent("网易");
		/*
		 * createSearchEngineData(); createSearchEngineData();
		 * createSearchEngineData(); createSearchEngineData();
		 * createSearchEngineData(); createSearchEngineData();
		 * System.out.println("---"); Thread thread = new Thread() {
		 * 
		 * @Override public void run() { while (true) { try { //
		 * searcherManager.maybeRefresh(); TimeUnit.MILLISECONDS.sleep(1000);
		 * searchContent("搜狐"); System.out.println("---"); } catch (Exception e)
		 * { e.printStackTrace(); } } } }; thread.start();
		 */
		/*
		 * createSearchEngineData(); searchContent(".pubA");
		 * System.out.println("---");
		 */
	}

	/**
	 * 
	 * Function: 搜索内容
	 * 
	 * @param keyWord
	 */
	public static void searchContent(String keyWord) {
		List<Content> searcheResult = new ArrayList<Content>();
		try {
			Version v = Version.LUCENE_42;
			// 取得查询对象
			IndexReader[] readers = SearchObject.getInstance()
					.getSearcherReads();

			MultiReader mReaders = new MultiReader(readers);
			IndexSearcher indexSearch = new IndexSearcher(mReaders);
			Analyzer analyzer = new StandardAnalyzer(v);

			// 创建boolean查询
			BooleanQuery query = new BooleanQuery();
			String[] field = { "title", "content" };
			BooleanClause.Occur[] flags = new BooleanClause.Occur[2];
			flags[0] = BooleanClause.Occur.SHOULD;
			flags[1] = BooleanClause.Occur.SHOULD;
			Query query1 = MultiFieldQueryParser.parse(v,
					QueryParser.escape(keyWord), field, flags, analyzer);
			query.add(query1, Occur.MUST);
			TopScoreDocCollector topCollector = TopScoreDocCollector.create(
					10000, true);
			indexSearch.search(query, topCollector);
			// 取得查询结果
			TopDocs topDocs = topCollector.topDocs();
			int resultCount = topDocs.totalHits;
			for (int i = 0; i < resultCount; i++) {
				Document doc = indexSearch.doc(topDocs.scoreDocs[i].doc);

				Content content = PackContentObject.convertDocToContent(doc,
						query1, analyzer);
				System.out.println(content.getTitle());
				// System.out.println(doc.get("content").substring(0,200));
			}
			System.out.println(resultCount);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
