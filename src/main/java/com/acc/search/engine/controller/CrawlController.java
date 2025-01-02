package com.acc.search.engine.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acc.search.engine.service.CrawlerService;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/crawler")
public class CrawlController {
	
	@Autowired
	CrawlerService crawlerService;
	
	@RequestMapping("/fetchUrls")
    public Map<String, Object> getURL(@RequestParam("url") String url)
	 {
        Long startTime = System.currentTimeMillis();
        Long endTime = System.currentTimeMillis();
        List<String> urlList = crawlerService.getURLList(url);
        Long time = endTime - startTime;
        Map<String, Object> model = new HashMap<>();
        model.put("urlList", urlList);
        model.put("numURLs", urlList.size());
        model.put("timeUsage_url", time);
        return model;
	 }
	@GetMapping("/fetch-content")
    public Map<String, Object> fetchContent(@RequestParam String url, @RequestParam(defaultValue = "1") int depth) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = crawlerService.fetchAndIndexContent(url, depth);
        long endTime = System.currentTimeMillis();

        result.put("timeUsageMs", endTime - startTime);
        result.put("depth", depth);
        return result;
    }
    

}
