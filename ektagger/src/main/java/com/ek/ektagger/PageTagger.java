package com.ek.ektagger;

import java.io.IOException;
import java.net.URL;

public interface PageTagger {
	
	public String getText (URL url) throws IOException;
	
	public String tagText(String text);
	
}