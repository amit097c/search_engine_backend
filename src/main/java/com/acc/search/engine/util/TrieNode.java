package com.acc.search.engine.util;

import java.util.*;

public class TrieNode
 {
	Map<Character, TrieNode> children;
	Integer index;
	public TrieNode() 
	 {
		this.children=new HashMap();
		this.index=null;
	 }
	
 }
