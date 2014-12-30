package com.lin.search;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.LogDocMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.index.TrackingIndexWriter;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.lin.util.ConfigConstant;

/**
 * 
 * @author hackcoder
 */
public class SearchEngineCore {

	public String INDEX_PATH = ConfigConstant.INDEX_PATH;
	private IndexWriter writer;
	private Analyzer analyzer;
	private Version version = ConfigConstant.version;
	private TrackingIndexWriter tkWriter;
	private SearcherManager mgr;
	private Directory directory;
	private ControlledRealTimeReopenThread<IndexSearcher> crtThread;

	public SearchEngineCore() {

	}

	/**
	 * 加载索引配置
	 * 
	 * @param index
	 */
	public SearchEngineCore(final String index, final String indexPath) {
		try {
			// 索引文件路径
			INDEX_PATH = indexPath;
			// 创建索引目录
			directory = FSDirectory.open(new File(INDEX_PATH));
			// IK分词器
			analyzer = new IKAnalyzer();
			LogMergePolicy mergePolicy = new LogDocMergePolicy();
			// 索引基本配置
			// 设置segment添加文档(Document)时的合并频率
			// 值较小,建立索引的速度就较慢
			// 值较大,建立索引的速度就较快,>10适合批量建立索引
			mergePolicy.setMergeFactor(5);
			// 设置segment最大合并文档(Document)数
			// 值较小有利于追加索引的速度
			// 值较大,适合批量建立索引和更快的搜索
			mergePolicy.setMaxMergeDocs(1000);
			// 启用复合式索引文件格式,合并多个segment
			// mergePolicy.setUseCompoundFile(true);

			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
					version, analyzer);
			indexWriterConfig.setMaxBufferedDocs(10000);
			indexWriterConfig.setMergePolicy(mergePolicy);
			indexWriterConfig.setRAMBufferSizeMB(50);
			// /设置索引的打开模式 创建或者添加索引
			indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
			// 如果索引文件被锁，解锁索引文件
			if (IndexWriter.isLocked(directory)) {
				IndexWriter.unlock(directory);
			}
			// 创建索引器
			writer = new IndexWriter(directory, indexWriterConfig);
			// 最开始创建索引时必须先提交，不然引起读取方法报错
			writer.commit();

			SearcherFactory searcherFactory = new SearcherFactory();
			mgr = new SearcherManager(writer, false, searcherFactory);

			// 创建IndexWriter 写入监听线程 5.0为创建5个线程，执行频率为0.025秒
			// 实现近实时搜索
			tkWriter = new TrackingIndexWriter(writer);
			crtThread = new ControlledRealTimeReopenThread<IndexSearcher>(
					tkWriter, mgr, 5.0, 0.025);
			crtThread.setDaemon(true);
			crtThread.setName("hackcoder real time search thread");
			crtThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Function:取得索引对象
	 * 
	 * @author JLC
	 * @return
	 * @throws IOException
	 */
	public IndexSearcher getSearcher() {
		IndexSearcher searcher = null;
		try {
			// 更新看看内存中索引是否有变化如果，有一个更新了，其他线程也会更新
			mgr.maybeRefresh();
			searcher = mgr.acquire();
			return searcher;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void releaseSearcher(IndexSearcher searcher) {
		try {
			mgr.release(searcher);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 提交索引
	 */
	public void commitIndex() {
		try {
			writer.commit();
			// writer.forceMerge(3);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 合并索引
	 */
	public void forceMerge() {
		try {
			writer.forceMerge(3);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭索引
	 */
	public void closeAll() {
		try {
			if (writer != null)
				writer.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 刷新数据
	 */
	public void refreshData() {
		try {
			mgr.maybeRefresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Version getVersion() {
		return version;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public Directory getDirectory() {
		return directory;
	}

	public IndexWriter getWriter() {
		return writer;
	}

	public TrackingIndexWriter getTw() {
		return tkWriter;
	}
}
