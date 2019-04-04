package com.ek.ektagger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

@Component
public class PageTaggerImpl implements PageTagger {
	
	@Autowired
	private MaxentTagger maxentTagger;
	
	private static final Logger log = LogManager.getLogger(PageTaggerImpl.class);
	
	private final String userAgent;
	
	PageTaggerImpl() {
		userAgent = "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36";
	}
	
	PageTaggerImpl(String userAgent) {
		this.userAgent = userAgent;
	}
	
	public String getText(URL url) throws IOException {
		
		URLConnection conn = url.openConnection();

		//Spoof the connection so we look like a web browser
		conn.setRequestProperty("User-Agent", userAgent);
		conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
        conn.setRequestProperty("Accept-Charset", "utf-8");
        conn.setRequestProperty("Accept-Encoding", "identity");
        
		BufferedReader reader = null;
		StringBuilder buffer = new StringBuilder();
		try {
	        reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
	        
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1) {
	            buffer.append(chars, 0, read); 
	        }
	        
	    } finally {
	        if (reader != null)
	            reader.close();
	    }
		
		//TODO: Optimize parsing
		String html = buffer.toString();
		//html = html.replace("&amp;", "'");
		// If there is escaped code we want to unescape so we can get rid of it
		html = StringEscapeUtils.unescapeHtml4(html);
		html = html.replaceAll("[0-9]+", "");
		
		String body = Jsoup.parse(html).body().text();
		
		// If there is escaped code we want to unescape so we can get rid of it
		//body = StringEscapeUtils.unescapeHtml4(body);
		
		// Some sites have encoded tags and seem to show up on the first parse, double parse to remove them.
		Document doc = Jsoup.parse(body);
		
		String bodyText = doc.text();
		// If there is escaped code we want to unescape so we can get rid of it
		//bodyText = StringEscapeUtils.unescapeHtml4(bodyText);
		log.debug("Body Text: " + bodyText);
		return bodyText;
	}

	public String tagText(String text) {
		final String taggedString = maxentTagger.tagString(text);
		log.debug("Tagged String: " + taggedString);
		return taggedString;
	}

	public String getUserAgent() {
		return userAgent;
	} 
	
	
	
}