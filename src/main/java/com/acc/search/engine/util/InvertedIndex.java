package com.acc.search.engine.util;

import java.nio.file.Paths;
import java.util.*;

public class InvertedIndex
{
	   private TrieNode root;
	   private Map<String,Set<String>> occurenceLists;
	   public InvertedIndex()
	    {
		   this.root=new TrieNode();
		   this.occurenceLists=new HashMap<>();	   
	    }   
	   public void addTerm(String term,String url) 
	    {
		   TrieNode node = root;
		   for(char ch:term.toCharArray()) 
		     {
			   node.children.putIfAbsent(ch, new TrieNode());
			   node=node.children.get(ch);			   
		     }
		   if(node.index==null)
		    {
			   node.index=term.hashCode();
		    }
		   occurenceLists.putIfAbsent(term, new HashSet<>());
		   occurenceLists.get(term).add(url);		   
	    }
	   public Set<String> searchTerm(String term)
	    {
		   return occurenceLists.getOrDefault(term, Collections.emptySet());
	    } 
	   
	   //query for pages containing all given terms
	   public Set<String> query(String[] terms)
	    {
		   List<Set<String>> lists = new ArrayList<>();
		   for(String term:terms) 
		    {
			   Set<String> urls=searchTerm(term);
			   if(urls.isEmpty()) 
			    {
				   return Collections.emptySet();
			    }
			   lists.add(urls);
		    }
		   Set<String> resultSet=new HashSet(lists.get(0));
		   for(int i=1;i<lists.size();i++) 
		    {
			   resultSet.retainAll(lists.get(i));
		    }
		   return resultSet;
	    } 
	    // Map a URL to its file path
	    public String getFilePath(String url) {
	        String relativePath = "src/main/resources/static/dat/text";
	        String sanitizedFileName = sanitizeFileName(url) + ".txt";
	        return Paths.get(relativePath, sanitizedFileName).toAbsolutePath().toString();
	    }

	    // Sanitization logic (same as used in writeContentToFile)
	    private String sanitizeFileName(String url) {
	        return url.replaceAll("[^a-zA-Z0-9]", "_");
	    }
}
