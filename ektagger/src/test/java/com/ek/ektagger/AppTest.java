package com.ek.ektagger;

//Let's import statically so that the code looks clearer
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext.xml" })
public class AppTest {

	@Autowired
	private PageTagger pageTagger;
	
	private static  String dummyHTML = "<html><head><title>Simple Page</title></head><body><script>var a = 1; var b=2;</script><p>hello</p><p>world</p></body></html>";

	@Test
	public void testTagger() throws Exception {
		
		PageTagger mockTagger = mock(PageTagger.class); 
		
		when(mockTagger.getText(any(URL.class))).thenReturn("hello world");

		String text = mockTagger.getText(new URL("http://foo"));
		String taggedText = pageTagger.tagText(text);
		
		assertEquals("hello_UH world_NN " , taggedText);
	}
	
	@Test
	public void testMockUrl() throws Exception {
		URL url = getMockUrl();
		String text = pageTagger.getText(url);
		System.out.println("URL:" + url + " Text:" + text);
		checkIfTextClean(text);
		System.out.println("URL:" + url + " Tags:" + pageTagger.tagText(text));
	}
	
	@Test
	public void testJsoup() {
		Document doc = Jsoup.parse("<html><head><title>Simple Page</title></head><body><script>var a = 1; var b=2;</script><p>hello</p><p>world</p></body></html>");
		final String bodyText = doc.body().text();
		assertEquals("hello world" , bodyText);
	}
	
	@Test
	@Ignore("Integration Test")
	public void testURLS() throws Exception {
		App.main(new String[]{"http://gumgum.com/"});
		App.main(new String[]{"http://www.popcrunch.com/jimmy-kimmel-engaged/"});
		App.main(new String[]{"http://gumgum-public.s3.amazonaws.com/numbers.html"});
		App.main(new String[]{"http://www.windingroad.com/articles/reviews/quick-drive-2012-bmw-z4-sdrive28i/"});
	}
	
	@Test
	public void testGumGum() throws Exception {
		//Integration test
		URL url = new URL("http://gumgum.com/");
		String text = pageTagger.getText(url);
		System.out.println("URL:" + url + " Text:" + text);
		checkIfTextClean(text);
		System.out.println("URL:" + url + " Tags:" + pageTagger.tagText(text));
	}
	
	@Test
	public void testPopcrunch() throws Exception {
		//Integration test
		URL url = new URL("http://www.popcrunch.com/jimmy-kimmel-engaged/");
		String text = pageTagger.getText(url);
		System.out.println("URL:" + url + " Text:" + text);
		checkIfTextClean(text);
		System.out.println("URL:" + url + " Tags:" + pageTagger.tagText(text));
	}
	
	@Test
	public void testGumGumNumber() throws Exception {
		//Integration test
		URL url = new URL("http://gumgum-public.s3.amazonaws.com/numbers.html");
		String text = pageTagger.getText(url);
		System.out.println("URL:" + url + " Text:" + text);
		checkIfTextClean(text);
		System.out.println("URL:" + url + " Tags:" + pageTagger.tagText(text));
	}
	
	@Test
	public void testWindingroad() throws Exception {
		//Integration test
		URL url = new URL("http://www.windingroad.com/articles/reviews/quick-drive-2012-bmw-z4-sdrive28i/");
		String text = pageTagger.getText(url);
		System.out.println("URL:" + url + " Text:" + text);
		checkIfTextClean(text);
		System.out.println("URL:" + url + " Tags:" + pageTagger.tagText(text));
	}
	
	private void checkIfTextClean(String text) {
		assertTrue(!text.contains("href="));
		assertTrue(!text.contains("src="));
		assertTrue(!text.contains("alt="));
	}

	@Before
	public void beforeTests() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	public static URL getMockUrl() throws IOException {
	    
	    final URLConnection mockConnection = Mockito.mock(URLConnection.class);
	    given(mockConnection.getInputStream()).willReturn(
	            new ByteArrayInputStream(dummyHTML.getBytes()));

	    final URLStreamHandler handler = new URLStreamHandler() {

	        @Override
	        protected URLConnection openConnection(final URL arg0)
	                throws IOException {
	            return mockConnection;
	        }
	    };
	    final URL url = new URL("http://foo.bar", "foo.bar", 80, "", handler);
	    return url;
	}
}