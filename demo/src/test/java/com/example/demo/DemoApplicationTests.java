package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests {

	@Value(value="${local.server.port}")
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void testfindAllBooks() throws Exception {
		assertThat(this.restTemplate.getForObject("http://localhost:9090/api/book",
				String.class)).isNotEmpty();
	}
	
	@Test
	public void testfindBookById() throws Exception {
		//book exists
		assertThat(this.restTemplate.getForObject("http://localhost:9090/api/book/3937382d332d31362d3134383431302d31",
				String.class)).isNotEmpty();
		
		//book doesn't exist
		assertThat(this.restTemplate.getForObject("http://localhost:9090/api/book/3977382d332d31362d3134383431302d31",
				String.class)).contains("doesn't exist in library");
	}
	
	@Test
	public void testSaveBook() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("isbn", "978-3-16-158511-0");
		map.put("title", "title of my bok");
		map.put("author", "joe");
		map.put("genre", "adventure");
		
		//1st entry
		assertThat(this.restTemplate.postForObject("http://localhost:9090/api/book", map, String.class)).doesNotContain("book with that isbn already exists");
		
		//repeat call
		assertThat(this.restTemplate.postForObject("http://localhost:9090/api/book", map, String.class)).contains("book with that isbn already exists");

	}
	
	@Test
	public void testDeleteBook() throws Exception {

		//1st entry and successful delete
		this.restTemplate.delete("http://localhost:9090/api/book/3937382d332d31362d3135383531312d30");
		assertThat(this.restTemplate.getForObject("http://localhost:9090/api/book/3937382d332d31362d3135383531312d30",
				String.class)).contains("doesn't exist in library");
		

	}
	
	@Test
	public void testUpdateBookDetails() throws Exception {

		Map<String, String> map = new HashMap<>();
		map.put("isbn", "978-3-16-158511-0");
		map.put("title", "title of my bok to updayr");
		map.put("author", "joe");
		map.put("genre", "adventure");
		
		//base entry
		assertThat(this.restTemplate.postForObject("http://localhost:9090/api/book", map, String.class)).doesNotContain("book with that isbn already exists");
		
		//good case
		map.put("title", "harry potter and the sorcerrer's stone");
		map.put("author", "jk rowling");
		map.put("genre", "adventure");
		this.restTemplate.put("http://localhost:9090/api/book/3937382d332d31362d3135383531312d30", map);
		assertThat(this.restTemplate.getForObject("http://localhost:9090/api/book/3937382d332d31362d3135383531312d30",
				String.class)).contains("harry potter and the sorcerrer's stone");
		
		
		//missing fields
		Map<String, String> missingFields = new HashMap<>();
		missingFields.put("title", "harry potter: chamber of secrets");
		this.restTemplate.put("http://localhost:9090/api/book/3937382d332d31362d3135383531312d30", map);
		assertThat(this.restTemplate.getForObject("http://localhost:9090/api/book/3937382d332d31362d3135383531312d30",
				String.class)).contains("harry potter and the sorcerrer's stone");
		
		
		//entry doesnt exist
		Map<String, String> missingEntry = new HashMap<>();
		missingEntry.put("title", "harry potter: chamber of secrets");
		missingEntry.put("genre", "adventure");
		map.put("author", "jk rowling");
		this.restTemplate.put("http://localhost:9090/api/book/3937382d332d31362d3135383531312d99", map);

		assertThat(this.restTemplate.getForObject("http://localhost:9090/api/book/3937382d332d31362d3135383531312d99",
				String.class)).contains("doesn't exist in library");
		
		
		//cleanup
		this.restTemplate.delete("http://localhost:9090/api/book/3937382d332d31362d3135383531312d30");

		

	}
	
	
	
	
}