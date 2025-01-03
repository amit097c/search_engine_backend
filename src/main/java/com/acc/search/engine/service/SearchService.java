package com.acc.search.engine.service;

import java.io.IOException;
import java.util.*;

import com.acc.search.engine.dto.Doc;
import com.acc.search.engine.util.PriorityQueue;
/*
public interface SearchService<K,V> {

	com.acc.search.engine.util.PriorityQueue<Integer, String> occurencesOfQueryString(String scan) throws IOException;

	Doc[] queue2List(PriorityQueue<Integer, String> pq) throws IOException;
}
*/
public interface SearchService {
    Set<String> search(String query);
    String spellCheck(String query);
}