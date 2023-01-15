package com.example.demo.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;

@RestController
@RequestMapping("/api/book")
public class LibraryController {
 
    @Autowired
    private BookRepository bookRepository;
        
    @GetMapping
    public List<Book> findAllBooks() {
    	
    	List<Book> books = new ArrayList<>();
        bookRepository.findAll().forEach(books::add);
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

    	 Optional<Book> book = bookRepository.findById(id);
    	 
    	    if(book.isPresent()) {
    	    	map.put("status", 1);
    			map.put("message", "Book was found");
    			map.put("data", book.get());
    			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
    	    } else {
    	    	map.put("status", 0);
    			map.put("message", "book with id: "+ id + " doesn't exist in library");
    			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.BAD_REQUEST);
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
    		map.put("status", 0);
			map.put("message", "Missing fields so cannot add entry");
			return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    	}
    	
    	String removeTrailingSpaces = book.getIsbn().trim();
    	String hexcode = toHex(removeTrailingSpaces);
    	book.setDbId(hexcode);
    	
    	
    	//check to see if book already exists
    	Optional<Book> bookFound = bookRepository.findById(hexcode);
    	System.out.println("id: " + book.toString());
		

    	if(!bookFound.isPresent()) {
    		bookRepository.saveAndFlush(book);
    		map.put("status", 1);
			map.put("message", "Book was added to library! The entry id is: "+ hexcode);
			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);

 	    } else {
 	    	map.put("status", 0);
			map.put("message", "book with that isbn already exists: "+ book.getTitle());
			return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
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

    	Optional<Book> bookFound =bookRepository.findById(id);
        
        if(bookFound.isPresent()) {
    		bookRepository.deleteById(id);
    		map.put("status", 1);
			map.put("message", "Book successfully removed from library");
			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
			
 	    } else {
 	    	map.put("status", 0);
			map.put("message", "Book with id: " + id + " doesnt exist");
			return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
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
    		map.put("status", 0);
			map.put("message", "Missing fields so cannot update entry");
			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.BAD_REQUEST);
    	}
    	
    	Optional<Book> bookFound =bookRepository.findById(id.trim());
    	 if(!bookFound.isPresent()) {
    		 map.put("status", 0);
 			map.put("message", "Book with id: " + id +" doesnt exist so cannot update");
 			return new ResponseEntity<Map<String,Object>>(map, HttpStatus.NOT_FOUND);
 			
  	    } 
    	
    	Book toUpdate = bookRepository.getReferenceById(id);
    	toUpdate.setAuthor(updatedItem.getAuthor());
    	toUpdate.setGenre(updatedItem.getGenre());
    	toUpdate.setIsbn(updatedItem.getIsbn());
    	toUpdate.setTitle(updatedItem.getTitle());
    	
    	
    	
    	
    	bookRepository.saveAndFlush(toUpdate);

		map.put("status", 1);
		map.put("message", "Successfully updated book with id: " + id);
		return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
		
    	
    	
    	
    }
    
    
    public String toHex(String input) {
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