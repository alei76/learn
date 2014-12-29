package com.lin.util;

import java.io.IOException;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.util.Version;

import com.lin.model.ContentObject;

/**
 * 不同对象之间转换
 * 
 * @author JLC From liutime.com
 * @version 1.0
 * @date 2012-2-6
 */
public class PackContentObject {

	/**
	 * 转换内容对象为Document对象
	 * 
	 * @param ct
	 * @return
	 */
	public static Document convertContentToDoc(ContentObject ct) {
		Document doc = new Document();
		// Field.Store.YES 表示存储内容
		doc.add(new StringField("title", ct.getTitle(), Field.Store.YES));
		doc.add(new TextField("content", ct.getContent(), Field.Store.YES));
		doc.add(new LongField("createDate", new Date().getTime(),
				Field.Store.YES));
		return doc;
	}

	/**
	 * 转换Documnet对象为内容对象
	 * 
	 * @param doc
	 * @return
	 */
	public static ContentObject convertDocToContent(Document doc) {
		ContentObject ct = new ContentObject();
		ct.setTitle(doc.get("title"));
		ct.setContent(doc.get("content"));
		Date dt = new Date(Long.parseLong(doc.get("createDate")));
		ct.setCreateDate(dt);
		return ct;
	}

	/**
	 * 转换Documnet对象为内容对象 针对关键字加亮
	 * 
	 * @param doc
	 * @return
	 */
	public static ContentObject convertDocToContent(Document doc, Query query,
			Analyzer analyzer) {
		ContentObject ct = new ContentObject();
		ct.setTitle(highligher(doc.get("title"), query, "title"));
		ct.setContent(doc.get("content"));
		Date dt = new Date(Long.parseLong(doc.get("createDate")));
		ct.setCreateDate(dt);
		return ct;
	}

	/**
	 * Function:关键字加亮
	 * 
	 * @author JLC
	 * @param text
	 *            原内容
	 * @param query
	 *            搜索的关键字
	 * @param field
	 *            要加亮的栏位
	 * @return
	 */
	private static String highligher(String text, Query query, String field) {
		try {
			QueryScorer scorer = new QueryScorer(query);
			Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
			// 关键字被包住的内容
			Formatter formatter = new SimpleHTMLFormatter(
					"<font style='color:red'>", "</font>");
			Highlighter lighter = new Highlighter(formatter, scorer);
			lighter.setTextFragmenter(fragmenter);
			String ht = lighter.getBestFragment(new StandardAnalyzer(
					Version.LUCENE_42), field, text);
			if (ht == null) {
				// 当内容超过两百字时，截取字符串
				if (text.length() >= 200) {
					text = text.substring(0, 200);
					text = text + "....";
				}
				return text;
			} else
				return ht.trim();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			e.printStackTrace();
		}
		return text;
	}
}
