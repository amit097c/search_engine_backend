package com.acc.search.engine.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import com.acc.search.engine.service.CrawlerService;
import com.acc.search.engine.util.InvertedIndex;
import com.acc.search.engine.util.HtmlToTextConverter;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CrawlerServiceImpl implements CrawlerService
 {
	private final Set<String> indexedPages = ConcurrentHashMap.newKeySet();
    @Override
    public void crawlAndIndex(InvertedIndex invertedIndex)
     {
   	  // Specify the directory containing the .txt files

    	String directoryPath = "src/main/resources/static/dat/text";
        Path dir = Paths.get(directoryPath);
        
        // Check if the directory exists
        if (!Files.exists(dir) || !Files.isDirectory(dir))
         {
            System.err.println("Directory does not exist: " + directoryPath);
            return;
         }

        try 
         {
            // Use Files.walk to traverse the directory and filter for .txt files
            Files.walk(dir)
                .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".txt"))
                .forEach(file -> {
                    try {
                        // Read the content of the file
                        String content = Files.readString(file);
                        // Extract the file name (used as the document identifier)
                        String fileName = file.getFileName().toString();
                        // Process the content to update the inverted index
                        processFileContent(fileName, content, invertedIndex);
                    } catch (IOException e) {
                        System.err.println("Failed to read file: " + file.getFileName() + " - " + e.getMessage());
                    }
                });
         }
        catch (IOException e) {
            System.err.println("Failed to traverse directory: " + e.getMessage());
        }
     }
    
    /**
     * Processes the content of a file and updates the inverted index.
     *
     * @param fileName      The name of the file being processed.
     * @param content       The content of the file.
     * @param invertedIndex The inverted index to update.
     */
    private void processFileContent(String fileName, String content, InvertedIndex invertedIndex)
     {
        // Normalize the content (e.g., convert to lowercase, remove punctuation)
        String normalizedContent = content.toLowerCase().replaceAll("[^a-z0-9\\s]", "");
        
        // Split the content into words
        String[] words = normalizedContent.split("\\s+");

        // Add each word to the inverted index
        for (String word : words)
         {
            if (!word.isEmpty())
             { 
            	// Skip empty strings
                invertedIndex.addTerm(word, fileName);
             }
         }
     }
	@Override
	public List<String> getURLList(String url)
	 {
	    List<String> urls = new ArrayList<>();
	    try
	     {
            // Fetch the HTML content of the page
            Document document = Jsoup.connect(url).get();
            
            // Select all anchor tags with href attributes
            Elements links = document.select("a[href]");
            for(Element link : links)
              {
                String href = link.attr("abs:href"); // Extract absolute URLs
                if(!href.isEmpty() && !urls.contains(href))
                 {
                    urls.add(href);
                 }
              }
         } 
	    catch (IOException e)
	     {
            System.err.println("Error fetching the URL: " + url + " - " + e.getMessage());
         }
	    return urls;
	 }

	@Override
	public Map<String, Object> fetchAndIndexContent(String url, int depth)
	 {
		Map<String, Object> response = new HashMap<>();
        Set<String> links = new HashSet<>();
        try
         {
            fetchLinksRecursively(url, depth, links);
            indexedPages.addAll(links);
            String content = fetchContent(url);            
            response.put("url", url);
            response.put("content", content);
            response.put("links", links);
         }
        catch (Exception e)
         {
            response.put("error", "Unable to fetch content from the provided URL: " + e.getMessage());
         }
        return response;
	 }
	private void fetchLinksRecursively(String url, int depth, Set<String> links) throws IOException
	 {
        if (depth == 0 || links.contains(url)) return;

        links.add(url); // use directed graph here
        Document doc = Jsoup.connect(url).get();
        String content = fetchContent(url);
        writeContentToFile(url, content);
        Elements elements = doc.select("a[href]");

        for (Element element : elements)
          {
            String link = element.absUrl("href");
            if (!link.isEmpty() && !links.contains(link))
             {
                fetchLinksRecursively(link, depth - 1, links);
             }
          }
     }
	
	// Helper method to write content to a .txt file
	private void writeContentToFile(String url, String content)
	 {
		    String relativePath = "src/main/resources/static/dat/text";
		    // Create the directory if it does not exist
		    Path directoryPath = Paths.get(relativePath);
		    try
		      {
		        if(!Files.exists(directoryPath))
		         {
		            Files.createDirectories(directoryPath);
		         }
		      }
		    catch (IOException e)
		      {
		        System.err.println("Failed to create directories: " + e.getMessage());
		        return;
		      }

		    // Generate the file name and construct the file path
	    String fileName = sanitizeFileName(url) + ".txt"; // Sanitize file name
	    Path filePath = directoryPath.resolve(fileName);
	    System.out.println("File will be saved at: " + filePath.toAbsolutePath());

	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile())))
	     {
	        writer.write(content);	        
	        System.out.println("Content successfully written to file: " + fileName);
	     } catch (IOException e)
	     {
	        System.err.println("Failed to write content to file: " + e.getMessage());
	     }
	}

	// Helper method to sanitize URL for file name
	private String sanitizeFileName(String url)
	 {
	    return url.replaceAll("[^a-zA-Z0-9.-]", "_");
	 }
	private String fetchContent(String url) throws IOException
	 {
        Document doc = Jsoup.connect(url).get();
        return doc.body().text();
     }
	@Override
    public List<String> getIndexedPages()
	 {
        return new ArrayList<>(indexedPages);
     }

    @Override
    public void clearIndex()
     {
        indexedPages.clear();
     }
  }

