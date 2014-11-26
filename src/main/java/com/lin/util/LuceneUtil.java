package com.lin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneUtil {

	private Log log = LogFactory.getLog(LuceneUtil.class);
	private IndexWriter writer;
	private IndexReader reader;
	private static Tika tika = new Tika();
	/**
	 * 建立索引
	 * @param srcDriectory		 需要建立索引的文件位置
	 * @param indexDirectory 	索引放置位置
	 * @param analyzer 			解析器
	 * @param version			lucene版本
	 * @param openMode			打开方式（1.创建，2追加，3创建或追加）
	 * @throws IOException
	 * @throws TikaException 
	 */
	@SuppressWarnings("deprecation")
	public void diskIndex(File srcDriectory,File indexDirectory, Analyzer analyzer, Version version,OpenMode openMode )
			throws IOException, TikaException {
		if(!indexDirectory.exists()){
			indexDirectory.mkdirs();
		}
		FSDirectory fsd = FSDirectory.open(indexDirectory);
		IndexWriterConfig config = new IndexWriterConfig(version, analyzer);
		config.setOpenMode(openMode);
		writer = new IndexWriter(fsd, config);
		List<File> files = FileUtil.listFile(srcDriectory);
		Document doc = null;
		for (File file : files) {
			doc = new Document();
			doc.add(new Field("name", file.getName(), Store.YES, Index.ANALYZED));
			doc.add(new Field("path", file.getAbsolutePath(), Store.YES,
					Index.NO));
			doc.add(new Field("content", tikaParseFileToString(file), Store.YES,
					Index.ANALYZED));
			writer.addDocument(doc);
		}
		writer.commit();
	}



	
	
	/**
	 * 获取查询把柄
	 * @param indexDirectory
	 * @return
	 * @throws IOException
	 */
	public IndexSearcher getIndexSearch(File indexDiretory) throws IOException{
		Directory directory = FSDirectory.open(indexDiretory);
		return new IndexSearcher(reader.open(directory));
	}
	
	public String search(File indexDirectory,String word,Analyzer analyzer) throws IOException, ParseException{
		IndexSearcher indexSearch = getIndexSearch(indexDirectory);
		QueryParser parser = new QueryParser( "content",analyzer);
		Query query = parser.parse(word);
		TopDocs docs = indexSearch.search(query, 10);
		ScoreDoc[] sds = docs.scoreDocs;
		for(ScoreDoc sd:sds){
			Document document = indexSearch.doc(sd.doc);
			System.out.println("name==========="+document.get("name")+"path==========="+document.get("path")
					);
		}
		return null;
	}
	public String tikaParseFileToString(File file) throws IOException, TikaException{
		return tika.parseToString(file);
	}
	public static void main(String[] args)throws Exception {
			//new LuceneUtil().diskIndex(new File("d:\\lucene"), new File("d:\\luceneIndex"), new IKAnalyzer(), Version.LUCENE_4_10_2, OpenMode.CREATE);
			new LuceneUtil().search(new File("d:\\luceneIndex"),"接口",new IKAnalyzer());
		Tika tika = new Tika();
		String str = tika.parseToString(new FileInputStream("d:\\lucene\\IKAnalyzer中文分词器V2012_FF使用手册.pdf"));
		System.out.println(str);
	}
}
