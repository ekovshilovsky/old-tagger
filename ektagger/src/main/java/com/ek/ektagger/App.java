package com.ek.ektagger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Tagging App
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Tag Tester" );
        if(args.length != 1) {
        	System.out.println( "Usage: pageTagger [url]" );
        	return;
        }
        
        ClassPathXmlApplicationContext context = null;
        try {
	        context = new ClassPathXmlApplicationContext("applicationContext.xml");
			PageTagger tagger = (PageTagger) context.getBean("pageTagger");
			System.out.println("Starting Tagging for: " + args[0]);
			String text = tagger.getText(new URL(args[0]));
			String taggedString = tagger.tagText(text);
			System.out.println("Tagged String: " + taggedString);
		} catch (MalformedURLException e) {
			System.out.println( "URL provided is malformed: " + e);
		} catch (IOException e) {
			System.out.println( "Error Parsing HTML: " + e);

		} finally {
			if (context != null){
				context.close();
			}
		}
        


    }
}
