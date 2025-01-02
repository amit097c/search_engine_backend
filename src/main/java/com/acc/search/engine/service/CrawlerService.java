package com.acc.search.engine.service;

import java.util.List;
import java.util.Map;

import com.acc.search.engine.util.InvertedIndex;

public interface CrawlerService
 {
	
	public List<String> getURLList (String url);

	void crawlAndIndex(InvertedIndex invertedIndex);
	
	Map<String, Object> fetchAndIndexContent(String url, int depth);
	List<String> getIndexedPages();
	void clearIndex();

 }
