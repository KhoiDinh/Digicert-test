package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.*;
import com.example.demo.controller.LibraryController;
import com.example.demo.model.Book;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests {

	@Value(value="${local.server.port}")
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Autowired
	private LibraryController controller;

	@Test
	public void testFindAllBooks() throws Exception {
		List<Book> books = controller.findAllBooks();
		
		Assertions.assertNotEquals(0, books.size() );
		
		/*assertThat(this.restTemplate.getForObject("http://localhost:9090/api/book",
				String.class)).isNotEmpty();*/
	}
	
	@Test
	public void testFindBookById() throws Exception {
		//book exists
		ResponseEntity<Map<String, Object>> result = controller.findBookById("3937382d332d31362d3134383431312d30");
		Map<String, Object> body = result.getBody();
		Assertions.assertEquals(3, body.size() );
		Assertions.assertEquals(HttpStatus.OK, body.get("status"));
		Assertions.assertNotEquals(null,  body.get("data"));
		
		
		
		
		//book doesn't exist
		ResponseEntity<Map<String, Object>> resultEmpty = controller.findBookById("3977382d332d31362d3134383431302d31");
		Map<String, Object> bodyEmpty = resultEmpty.getBody();
		Assertions.assertEquals(2, bodyEmpty.size() );
		Assertions.assertEquals(HttpStatus.NOT_FOUND, bodyEmpty.get("status"));
		Assertions.assertEquals(true, bodyEmpty.get("message").toString().contains("doesn't exist in library"));
	}
	
	@Test
	public void testSaveBook() throws Exception {
		/*Map<String, String> map = new HashMap<>();
		map.put("isbn", "978-3-16-158511-0");
		map.put("title", "title of my bok");
		map.put("author", "joe");
		map.put("genre", "adventure");*/
		
		//book object
		Book newBook = new Book();
		newBook.setIsbn("978-3-16-158511-0");
		newBook.setTitle("title of my bok");
		newBook.setAuthor("joe");
		newBook.setGenre("advemture");
		
		//1st entry
		
		ResponseEntity<Map<String, Object>> response = controller.saveBook(newBook);
		Map<String, Object> bodyPass = response.getBody();
		Assertions.assertEquals(HttpStatus.OK, bodyPass.get("status"));
		Assertions.assertEquals(true, bodyPass.get("message").toString().contains("Book was added to library!"));
		
		//assertThat(this.restTemplate.postForObject("http://localhost:9090/api/book", map, String.class)).doesNotContain("book with that isbn already exists");
		
		//repeat call
		ResponseEntity<Map<String, Object>> responseDuplicate = controller.saveBook(newBook);
		Map<String, Object> bodyDupe = responseDuplicate.getBody();
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, bodyDupe.get("status"));
		Assertions.assertEquals(true, bodyDupe.get("message").toString().contains("Cannot add book. Book with that isbn already exists:"));
		//assertThat(this.restTemplate.postForObject("http://localhost:9090/api/book", map, String.class)).contains("book with that isbn already exists");
		
		
		//missing data in incoming book object
		Book badBook = new Book();
		newBook.setIsbn("178-4-06-128238-9");
		newBook.setTitle("missing");
		newBook.setAuthor("joe");
		
		
		ResponseEntity<Map<String, Object>> responseMissing = controller.saveBook(badBook);
		Map<String, Object> bodyMissing = responseMissing.getBody();
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, bodyMissing.get("status"));
		Assertions.assertEquals(true, bodyMissing.get("message").toString().contains("Missing fields so cannot add entry"));

	}
	
	@Test
	public void testDeleteBook() throws Exception {

		//1st entry and successful delete
		
		ResponseEntity<Map<String, Object>> response = controller.deleteBook("3937382d332d31362d3135383531312d30");
		Map<String, Object> bodyPass = response.getBody();
		Assertions.assertEquals(HttpStatus.OK, bodyPass.get("status"));
		Assertions.assertEquals(true, bodyPass.get("message").toString().contains("Book successfully removed from library"));


		//cannot delete because entry with id doesnt exist
		ResponseEntity<Map<String, Object>> responseBad = controller.deleteBook("3937382d33fd313b2d3135a83531319d30");
		Map<String, Object> bodyFail = responseBad.getBody();
		Assertions.assertEquals(HttpStatus.NOT_FOUND, bodyFail.get("status"));
		Assertions.assertEquals(true, bodyFail.get("message").toString().contains("doesnt exist"));
		
		/*this.restTemplate.delete("http://localhost:9090/api/book/3937382d332d31362d3135383531312d30");
		assertThat(this.restTemplate.getForObject("http://localhost:9090/api/book/3937382d332d31362d3135383531312d30",
				String.class)).contains("doesn't exist in library");*/
		

	}
	
	@Test
	public void testUpdateBookDetails() throws Exception {

		
		Book newBook = new Book();
		newBook.setIsbn("978-3-16-158511-0");
		newBook.setTitle("title of my bok to updayr");
		newBook.setAuthor("joe");
		newBook.setGenre("adventure");
		
		//base entry
		
		ResponseEntity<Map<String, Object>> response = controller.saveBook(newBook);
		Map<String, Object> bodyPass = response.getBody();
		Assertions.assertEquals(HttpStatus.OK, bodyPass.get("status"));
		Assertions.assertEquals(true, bodyPass.get("message").toString().contains("Book was added to library!"));
		
		String[] hold = bodyPass.get("message").toString().split(" ");
		String updateId = hold[hold.length-1];
		
		
		
		
	
		
		//good update
		Book updateBookDetails = new Book();
		updateBookDetails.setIsbn("978-3-16-158511-0");
		updateBookDetails.setTitle("harry potter and the sorcerrer's stone");
		updateBookDetails.setAuthor("jk rowling");
		updateBookDetails.setGenre("adventure");
		
		ResponseEntity<Map<String, Object>> updateResponse = controller.updateBookDetails(updateId, updateBookDetails);
		Map<String, Object> body = updateResponse.getBody();
		Assertions.assertEquals(HttpStatus.OK, body.get("status"));
		Assertions.assertEquals(true, body.get("message").toString().contains("Successfully updated book with id"));

		
		//missing entries
		Book missingEntries = new Book();

		missingEntries.setIsbn("978-3-16-158511-0");
		missingEntries.setTitle("harry potter fake booke");
		ResponseEntity<Map<String, Object>> missingResponse = controller.updateBookDetails(updateId, missingEntries);
		Map<String, Object> bodyMissing = missingResponse.getBody();
		Assertions.assertEquals(HttpStatus.BAD_REQUEST, bodyMissing.get("status"));
		Assertions.assertEquals(true, bodyMissing.get("message").toString().contains("Missing fields in request body so cannot update entry"));
		
		
		//id passed in doesnt exist
		Book doesntExist = new Book();
		doesntExist.setIsbn("978-3-16-158511-0");
		doesntExist.setTitle("harry potter and the chamber of secrets");
		doesntExist.setAuthor("jk rowling");
		doesntExist.setGenre("adventure");
		
		ResponseEntity<Map<String, Object>> doesntExistResponse = controller.updateBookDetails(updateId+"fake", doesntExist);
		Map<String, Object> bodyDoesntExist = doesntExistResponse.getBody();
		Assertions.assertEquals( HttpStatus.NOT_FOUND, bodyDoesntExist.get("status"));
		Assertions.assertEquals(true, bodyDoesntExist.get("message").toString().contains("doesnt exist so cannot update"));
		

		
		
		this.restTemplate.delete("http://localhost:9090/api/book/3937382d332d31362d3135383531312d30");

		

	}
	
	
	
	
}