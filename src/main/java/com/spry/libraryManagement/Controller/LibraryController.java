package com.spry.libraryManagement.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.spry.libraryManagement.DTO.BookDTO;
import com.spry.libraryManagement.DTO.StatusDTO;
import com.spry.libraryManagement.Model.AvailabilityStatus;
import com.spry.libraryManagement.Service.BookService;

@Controller
@RequestMapping("/library")
public class LibraryController {
	private final BookService bookService;
	
	@Autowired
	public LibraryController(BookService bookService) {
		this.bookService = bookService;
	}
	
	@GetMapping("/books")
	public ResponseEntity<String> getAllBooks() {
		StatusDTO status = bookService.getAllBooks();
		
		return new ResponseEntity<String>(new Gson().toJson(status), HttpStatus.OK);
	}
	
	// API TO ADD BOOK
	@PostMapping("/addBook")
	public ResponseEntity<String> addBook(@RequestBody BookDTO bookDTO) {
		StatusDTO status = bookService.addBook(bookDTO);
		
		return new ResponseEntity<String>(new Gson().toJson(status), HttpStatusCode.valueOf(status.getStatusCode()));
	}
	
	// FETCH PAGINATED BOOKS
	@GetMapping("/getPaginatedBooks")
	public ResponseEntity<Page<BookDTO>> getAll(@RequestParam Optional<String> author,
	                                         @RequestParam Optional<Integer> publishedYear,
	                                         @RequestParam Optional<AvailabilityStatus> availabilityStatus,
	                                         Pageable pageable) {
	    return ResponseEntity.ok(bookService.getPaginatedBookList(author, publishedYear, availabilityStatus, pageable));
	}
	
	// EDIT A BOOK DETAILS
	@PutMapping("/updateBook")
	public ResponseEntity<String> updateBook(@RequestBody BookDTO bookDTO) {
		StatusDTO status = bookService.updateBookDetails(bookDTO);
		
		return new ResponseEntity<String>(new Gson().toJson(status), HttpStatusCode.valueOf(status.getStatusCode()));
	}
	
	// SEARCH FOR BOOK BY TITLE OR AUTHOR USING PARTIAL MATCHING
	@GetMapping("/partialMatchSearch")
	public ResponseEntity<String> getBookUsingTitleAuthorPartialMatch(@RequestParam(required = false) String bookTitle, @RequestParam(required = false) String author) {
		StatusDTO status = bookService.getBookUsingTitleAuthorPartialMatch(bookTitle, author);
		
		return new ResponseEntity<String>(new Gson().toJson(status), HttpStatusCode.valueOf(status.getStatusCode()));
	}
	
	// REMOVE BOOK FROM INVENTORY
	@DeleteMapping("/deleteBook")
	public ResponseEntity<String> deleteBook(@RequestParam(required = true) String bookName) {
		StatusDTO status = bookService.deleteBook(bookName);
		
		return new ResponseEntity<String>(new Gson().toJson(status), HttpStatusCode.valueOf(status.getStatusCode()));
	}
	
	// BORROW BOOK
	@PutMapping("/borrowBook")
	public ResponseEntity<String> borrowBook(@RequestParam(required = true) String bookname) {
		StatusDTO status = bookService.borrowBook(bookname);
		
		return new ResponseEntity<String>(new Gson().toJson(status), HttpStatusCode.valueOf(status.getStatusCode()));
	}
	
	// RETURN BOOK
	@PutMapping("/returnBook")
	public ResponseEntity<String> returnBook(@RequestParam(required = true) String bookname) {
		StatusDTO status = bookService.returnBook(bookname);
		
		return new ResponseEntity<String>(new Gson().toJson(status), HttpStatusCode.valueOf(status.getStatusCode()));
	}
}
