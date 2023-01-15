package com.example.demo.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<Book> findBookById(@PathVariable(value = "id") String id) {
    	 Optional<Book> book = bookRepository.findById(id);
    	 
    	    if(book.isPresent()) {
    	    	System.out.println("ENTER");
    	        return ResponseEntity.ok().body(book.get());
    	    } else {
    	        return ResponseEntity.notFound().build();
    	    }
       
    }
    
 
    /**
     * Add book to library
     *
     * @param book the entity
     * @return id of new entry or failure response
     */
    @PostMapping
    public ResponseEntity<String> saveBook(@Validated @RequestBody Book book) {
    	if(book.getAuthor()== null|| book.getGenre()== null || book.getIsbn()== null|| book.getTitle() == null ) {
    		return ResponseEntity.badRequest().body("Missing fields so cannot add entry");
    	}
    	
    	String removeTrailingSpaces = book.getIsbn().trim();
    	String hexcode = toHex(removeTrailingSpaces);
    	book.setDbId(hexcode);
    	
    	
    	//check to see if book already exists
    	Optional<Book> bookFound = bookRepository.findById(hexcode);
    	System.out.println("id: " + book.toString());
    	 
    	if(!bookFound.isPresent()) {
    		bookRepository.saveAndFlush(book);
    		 return ResponseEntity.ok().body(hexcode);
 	    } else { 	    	
 	    	return ResponseEntity.badRequest().body("book with that isbn already exists: "+ book.getTitle());
 	    }
    }
    
    
    /**
     * Delete book entry.
     *
     * @param id the book id
     * @return string response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable(value = "id") String id) {
    	Optional<Book> bookFound =bookRepository.findById(id);
        
        if(bookFound.isPresent()) {
    		bookRepository.deleteById(id);
        	return ResponseEntity.ok().body("Book with id: " + id + " has been deleted");
 	    } else {
 	    	return ResponseEntity.badRequest().body("Book with id: " + id +" doesnt exist");
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
    public ResponseEntity<String> updateBookDetails(@PathVariable("id") String id,@RequestBody Book updatedItem) {

    	if(updatedItem.getAuthor()== null || updatedItem.getGenre()== null || updatedItem.getIsbn()== null|| updatedItem.getTitle()== null ) {
    		return ResponseEntity.badRequest().body("Missing fields so cannot update entry");
    	}
    	
    	Optional<Book> bookFound =bookRepository.findById(id.trim());
    	 if(!bookFound.isPresent()) {
    		 return ResponseEntity.badRequest().body("Book with id: " + id +" doesnt exist so cannot update");
  	    } 
    	
    	Book toUpdate = bookRepository.getReferenceById(id);
    	toUpdate.setAuthor(updatedItem.getAuthor());
    	toUpdate.setGenre(updatedItem.getGenre());
    	toUpdate.setIsbn(updatedItem.getIsbn());
    	toUpdate.setTitle(updatedItem.getTitle());
    	
    	String oldDbId = updatedItem.getDbId();
    	
    	
    	bookRepository.saveAndFlush(toUpdate);
    	return ResponseEntity.ok().body("Successfull updated book with id: " + oldDbId);
    	
    	
    	
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