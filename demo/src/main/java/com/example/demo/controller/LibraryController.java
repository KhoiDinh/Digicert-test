package com.example.demo.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import org.slf4j.*;



@RestController
@RequestMapping("/api/book")
public class LibraryController {

	@Autowired
	private BookRepository bookRepository;
	
	
	private Logger logger = LoggerFactory.getLogger(LibraryController.class);

	@GetMapping
	public List<Book> findAllBooks() {
		 
		logger.info("Searching for all books in library...");

		List<Book> books = new ArrayList<>();
		bookRepository.findAll().forEach(books::add);
		
		logger.info("Book found! Returning them...");
		return books;

	}

	/**
	 * get book details with specific id
	 *
	 * @param id the id of the book
	 * @return book entity, if exists
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> findBookById(@PathVariable(value = "id") String id) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		try {
			logger.info("Searching for book with id: "+ id);
			Optional<Book> book = bookRepository.findById(id);


			if(book.isPresent()) {
				logger.info("Book found! Returning information now...");

				map.put("status",HttpStatus.OK);
				map.put("message", "Book was found");
				map.put("data", book.get());
				return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
			} else {
				logger.error("book with id: "+ id + " doesn't exist in library");
				map.put("status", HttpStatus.NOT_FOUND);
				map.put("message", "book with id: "+ id + " doesn't exist in library");
				return new ResponseEntity<Map<String,Object>>(map, HttpStatus.NOT_FOUND);
			}
		}catch(Exception e) {
			logger.error("Cannot find book at this time. Reason: "+ e.getMessage());
			map.put("status", HttpStatus.NOT_FOUND);
			map.put("message", "Cannot find book at this time. Reason: "+ e.getMessage());
			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.NOT_FOUND);
		}


	}


	/**
	 * Add book to library
	 *
	 * @param book the entity
	 * @return id of new entry or failure response
	 */
	@PostMapping
	public ResponseEntity<Map<String, Object>> saveBook(@Validated @RequestBody Book book) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		if(book.getAuthor()== null|| book.getGenre()== null || book.getIsbn()== null|| book.getTitle() == null ) {
			logger.error("Cannot add book to library. Missing fields in entry.");
			map.put("status", HttpStatus.BAD_REQUEST);
			map.put("message", "Missing fields so cannot add entry");
			return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
		}

		String removeTrailingSpaces = book.getIsbn().trim();
		String hexcode = toHex(removeTrailingSpaces);
		book.setDbId(hexcode);


		//check to see if book already exists
		try {
			logger.info("Trying to add book to library...");
			Optional<Book> bookFound = bookRepository.findById(hexcode);
			System.out.println("id: " + book.toString());


			if(!bookFound.isPresent()) {
				logger.info("Book was added to library! The entry id is: "+ hexcode);
				bookRepository.saveAndFlush(book);
				map.put("status", HttpStatus.OK);
				map.put("message", "Book was added to library! The entry id is: "+ hexcode);
				return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);

			} else {
				logger.error("Cannot add book. Book with that isbn already exists: "+ book.getTitle());
				map.put("status", HttpStatus.BAD_REQUEST);
				map.put("message", "Cannot add book. Book with that isbn already exists: "+ book.getTitle());
				return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
			}
		} catch(Exception e) {
			logger.error( "Cannot add book to library at this time. Reason: "+ e.getMessage());
			map.put("status", HttpStatus.NOT_FOUND);
			map.put("message", "Cannot add book to library at this time. Reason: "+ e.getMessage());
			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.NOT_FOUND);
		}

	}


	/**
	 * Delete book entry.
	 *
	 * @param id the book id
	 * @return string response
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, Object>> deleteBook(@PathVariable(value = "id") String id) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		try {
			logger.info("Initiating delete process...");
			Optional<Book> bookFound =bookRepository.findById(id);

			if(bookFound.isPresent()) {
				logger.info("Book found! Trying to delete book from library...");
				bookRepository.deleteById(id);
				
				logger.info("Book successfully removed from library.");
				map.put("status", HttpStatus.OK);
				map.put("message", "Book successfully removed from library");
				return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);

			} else {
				logger.error("Book with id: " + id + " doesnt exist");
				map.put("status", HttpStatus.NOT_FOUND);
				map.put("message", "Book with id: " + id + " doesnt exist");
				return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
			}
		}catch(Exception e) {
			logger.error("Cannot delete book from library at this time. Reason: "+ e.getMessage());
			map.put("status", HttpStatus.NOT_FOUND);
			map.put("message", "Cannot delete book from library at this time. Reason: "+ e.getMessage());
			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.NOT_FOUND);
		}


	}

	/**
	 * Update book entry, not including the db id.
	 *
	 * @param id the book id
	 * @param updatedItem the updated book details
	 * @return string response
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Map<String, Object>> updateBookDetails(@PathVariable("id") String id,@RequestBody Book updatedItem) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		if(updatedItem.getAuthor()== null || updatedItem.getGenre()== null || updatedItem.getIsbn()== null|| updatedItem.getTitle()== null ) {
			logger.error("Cannot update book in library. Missing fields in entry.");
			
			map.put("status", HttpStatus.BAD_REQUEST);
			map.put("message", "Cannot update book in library. Missing fields in request body so cannot update entry");
			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.BAD_REQUEST);
		}

		logger.info("Initiating book update process...");
		try {
			Optional<Book> bookFound =bookRepository.findById(id.trim());
			if(!bookFound.isPresent()) {
				
				logger.error("Book with id: " + id + " doesnt exist so cannot update");
				
				map.put("status", HttpStatus.NOT_FOUND);
				map.put("message", "Book with id: " + id +" doesnt exist so cannot update");
				return new ResponseEntity<Map<String,Object>>(map, HttpStatus.NOT_FOUND);

			} 
			
			logger.info("Book found! Trying to update book details in library...");

			Book toUpdate = bookRepository.getReferenceById(id);
			toUpdate.setAuthor(updatedItem.getAuthor());
			toUpdate.setGenre(updatedItem.getGenre());
			toUpdate.setIsbn(updatedItem.getIsbn());
			toUpdate.setTitle(updatedItem.getTitle());

			bookRepository.saveAndFlush(toUpdate);
			
			logger.info("Successfully updated book with id: " + id);

			map.put("status", HttpStatus.OK);
			map.put("message", "Successfully updated book with id: " + id);
			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
		}catch(Exception e) {
			
			logger.error("Cannot update book details in library at this time. Reason: "+ e.getMessage());
			map.put("status", HttpStatus.NOT_FOUND);
			map.put("message", "Cannot update book details in library at this time. Reason: "+ e.getMessage());
			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.NOT_FOUND);
		}
	}


	public String toHex(String input) {
		
		logger.info("Generating unique id for book.");
		StringBuffer sb = new StringBuffer();
		//Converting string to character array
		char ch[] = input.toCharArray();
		for(int i = 0; i < ch.length; i++) {
			String hexString = Integer.toHexString(ch[i]);
			sb.append(hexString);
		}
		return sb.toString();
	}


}