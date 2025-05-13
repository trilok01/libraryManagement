package com.spry.libraryManagement.Service;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spry.libraryManagement.DAO.AuthorDAO;
import com.spry.libraryManagement.DAO.BookAuthorDAO;
import com.spry.libraryManagement.DAO.BookDAO;
import com.spry.libraryManagement.DTO.BookDTO;
import com.spry.libraryManagement.DTO.StatusDTO;
import com.spry.libraryManagement.Model.Author;
import com.spry.libraryManagement.Model.AvailabilityStatus;
import com.spry.libraryManagement.Model.Book;
import com.spry.libraryManagement.Model.BookAuthor;

@Service
public class BookService {
	private final BookDAO bookDAO;
	private final AuthorDAO authorDAO;
	private final BookAuthorDAO bookAuthorDAO;
	
	private static final Logger logger = LoggerFactory.getLogger(BookService.class);
	
	@Autowired
	public BookService(BookDAO bookDAO, AuthorDAO authorDAO, BookAuthorDAO bookAuthorDAO) {
		this.bookDAO = bookDAO;
		this.authorDAO = authorDAO;
		this.bookAuthorDAO = bookAuthorDAO;
	}
	
	public StatusDTO getAllBooks() {
		StatusDTO status = new StatusDTO();
		
		try {
			List<BookDTO> bookList = bookAuthorDAO.getAllBooks();
			
			status.setData(bookList);
			status.setMessage("Fetched book list successfully.");
			status.setStatusCode(HttpStatus.OK.value());
		} catch(Exception e) {
			logger.error("Something went wrong." + e);
			
			status.setMessage("Someting went wrong.");
			status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		
		return status;
	}
	
	@Transactional
	public StatusDTO addBook(BookDTO bookDTO) {
		StatusDTO status = new StatusDTO();
		
		try {
			// VALIDATE bookDTO
			status = validateBookEntry(bookDTO);
			
			if(status.getStatusCode() != 200) {
				return status;
			}
			
			// CHECK FOR EXISTING ISBN IN DB
			String isbnInDb = bookDAO.getISBN(bookDTO.getIsbn());
			
			if(isbnInDb != null && isbnInDb.equals(bookDTO.getIsbn())) {
				logger.info("Book with same ISBN is already present in the inventory. ISBN: " + bookDTO.getIsbn());
				
				status.setMessage("Book with same ISBN is already present in the inventory.");
				status.setStatusCode(HttpStatus.CONFLICT.value()); // Status code for conflict
				
				return status;
			}
			
			// CHECK IF AUTHOR IS PRESENT IN DB
			Author authorDetails = authorDAO.getAuthorByName(bookDTO.getAuthorName());
			
			int authorId = authorDetails != null ? authorDetails.getId() : 0;
			
			if(authorDetails == null) {
				// INSERT AUTHOR IN DB
				Author author = new Author();
				author.setName(bookDTO.getAuthorName());
				
				authorId = authorDAO.insertAuthor(author);
				logger.info("Successfully inserted author in database. Author: " + author.getName() + " Author Id: " + author.getId());
			}
			
			// INSERT BOOK IN DB
			Book book = new Book();
			book.setTitle(bookDTO.getBookTitle());
			book.setIsbn(bookDTO.getIsbn());
			book.setPublishedYear(bookDTO.getPublishedYear());
			book.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
			
			int bookId = bookDAO.addBook(book);
			
			logger.info("Successfully inserted book in database. BookId: " + bookId + " Book Name: " + book.getTitle());
			
			BookAuthor bookAuthor = new BookAuthor();
			bookAuthor.setAuthorId(authorId);
			bookAuthor.setBookId(bookId);
			
			bookAuthorDAO.addBookAuthorEntry(bookAuthor);
			logger.info("Successfully inserted entry in Book_Author table.");
			
			status.setMessage("Book added successfully.");
			status.setStatusCode(HttpStatus.OK.value());
		} catch(Exception e) {
			logger.error("Something went wrong." + e);
			
			status.setMessage("Something went wrong.");
			status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		
		return status;
	}
	
	private StatusDTO validateBookEntry(BookDTO bookDTO) {
		StatusDTO status = new StatusDTO();
		status.setStatusCode(HttpStatus.OK.value());
		StringBuilder message = new StringBuilder();
		
		if(bookDTO.getAuthorName() == null || bookDTO.getAuthorName() == "") {
			logger.info("Author Name can not be null/empty.");
			
			message.append("Author Name can not be null/empty. ");
			status.setStatusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
		}
		
		if(bookDTO.getBookTitle() == null || bookDTO.getBookTitle() == "") {
			logger.info("Book title can not be null/empty.");
			
			message.append("Book title can not be null/empty. ");
			status.setStatusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
		}
		
		if(bookDTO.getIsbn() == null || bookDTO.getIsbn() == "") {
			logger.info("ISBN can not be null/empty.");
			
			message.append("ISBN can not be null/empty. ");
			status.setStatusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
		}
		
		if(bookDTO.getPublishedYear() == null || bookDTO.getPublishedYear() > Year.now().getValue()) {
			logger.info("Published year can not be null or greater than current year.");
			
			message.append("Published year can not be null or greater than current year.");
			status.setStatusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
		}
		
		status.setMessage(message.toString());
		
		return status;
	}
	
	@Transactional
	public StatusDTO deleteBook(String bookName) {
		StatusDTO status = new StatusDTO();
		
		try {
			Book book = bookDAO.getBookByName(bookName);
			
			if(book == null) {
				logger.info("Book with name: '" + bookName + "' not found in db.");
				
				status.setMessage("Book not found.");
				status.setStatusCode(HttpStatus.NOT_FOUND.value());
				
				return status;
			}
			
			bookAuthorDAO.deleteEntryByBookId(book.getId());
			logger.info("Deleted entry in Book_Author table with associated book id");
			
			bookDAO.deleteBookById(book.getId());
			logger.info("successfully deleted book from db.");
			
			status.setMessage("Book deleted successfully.");
			status.setStatusCode(HttpStatus.OK.value());
			
		} catch(Exception e) {
			logger.error("Something went wrong." + e);
			
			status.setMessage("Something went wrong.");
			status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		
		return status;
	}
	
	@Transactional
	public StatusDTO borrowBook(String bookName) {
		StatusDTO status = new StatusDTO();
		
		try {
			Book book = bookDAO.getBookByName(bookName);
			
			if(book == null) {
				logger.info("Book with name: '" + bookName + "' not found in db.");
				
				status.setMessage("Book not found.");
				status.setStatusCode(HttpStatus.NOT_FOUND.value());
				
				return status;
			}
			
			if(book.getAvailabilityStatus().equals(AvailabilityStatus.BORROWED)) {
				logger.info("Book '" + bookName + "' is already borrowed by someone.");
				
				status.setMessage("Book '" + bookName + "' is already borrowed by someone. Try with different book");
				status.setStatusCode(HttpStatus.CONFLICT.value());
				
				return status;
			}
			
			bookDAO.updateBookAvailabilityById(book.getId(), AvailabilityStatus.BORROWED);
			logger.info("Book '" + bookName + "' borrowed successfully.");
			
			status.setMessage("Book '" + bookName + "' borrowed successfully.");
			status.setStatusCode(HttpStatus.OK.value());
			
		} catch(Exception e) {
			logger.error("Something went wrong." + e);
			
			status.setMessage("Something went wrong.");
			status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		
		return status;
	}
	
	@Transactional
	public StatusDTO returnBook(String bookName) {
		StatusDTO status = new StatusDTO();
		
		try {
			Book book = bookDAO.getBookByName(bookName);
			
			if(book == null) {
				logger.info("Book with name: '" + bookName + "' not found in db.");
				
				status.setMessage("Book not found. Please enter correct book name.");
				status.setStatusCode(HttpStatus.NOT_FOUND.value());
				
				return status;
			}
			
			if(book.getAvailabilityStatus().equals(AvailabilityStatus.AVAILABLE)) {
				logger.info("Book '" + bookName + "' is not borrowed yet.");
				
				status.setMessage("Book '" + bookName + "' is not borrowed yet. Borrow before returning");
				status.setStatusCode(HttpStatus.CONFLICT.value());
				
				return status;
			}
			
			bookDAO.updateBookAvailabilityById(book.getId(), AvailabilityStatus.AVAILABLE);
			logger.info("Book '" + bookName + "' returned successfully.");
			
			status.setMessage("Book '" + bookName + "' returned successfully.");
			status.setStatusCode(HttpStatus.OK.value());
			
		} catch(Exception e) {
			logger.error("Something went wrong." + e);
			
			status.setMessage("Something went wrong.");
			status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		
		return status;
	}
	
	@Transactional
	public StatusDTO updateBookDetails(BookDTO bookDTO) {
		StatusDTO status = new StatusDTO();
		
		try {
			
			status = validateBookEntry(bookDTO);
			
			if(status.getStatusCode() != 200) {
				return status;
			}
			
			Integer bookId = bookDTO.getBookId();
			
			if(bookId == null) {
				logger.info("Book Id can not be null.");
				
				status.setMessage("Book Id can not be null.");
				status.setStatusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
				
				return status;
			}
			
			Book book = bookDAO.getBookById(bookId);
			
			if(book == null) {
				logger.info("No book found with given id. Pleade select valid book.");
				
				status.setMessage("No book found with given id. Pleade select valid book.");
				status.setStatusCode(HttpStatus.NOT_FOUND.value());
				
				return status;
			}
			
			Author authorInDb = authorDAO.getAuthorByBookId(bookId);
			
			// CHECK IF AUTHOR IS UPDATED OR NOT
			if(!authorInDb.getName().equals(bookDTO.getAuthorName())) {
				logger.info("Updating author.");
				
				// REMOVE OLD ENTRY FROM Book_Author table
				bookAuthorDAO.deleteEntryByBookIdAuthorId(bookId, authorInDb.getId());
				
				// CHECK IF NEW AUTHOR IS PRESENT IN DB OR NOT
				Author newAuthor = authorDAO.getAuthorByName(bookDTO.getAuthorName());
				
				Integer newAuthorId = newAuthor == null ? null : newAuthor.getId();
				
				// AUTHOR IS NOT PRESENT IN DB
				if(newAuthor == null) {
					newAuthor = new Author();
					newAuthor.setName(bookDTO.getAuthorName());
					
					newAuthorId = authorDAO.insertAuthor(newAuthor);
					logger.info("New author added.");
				}
				
				// ADD NEW ENTRY IN Book_Author TABLE
				BookAuthor bookAuthor = new BookAuthor();
				bookAuthor.setAuthorId(newAuthorId);
				bookAuthor.setBookId(bookId);
				
				bookAuthorDAO.addBookAuthorEntry(bookAuthor);
				
				logger.info("Author updated successfully.");
			}
			
			bookDAO.updateBookDetails(bookDTO);
			logger.info("Book updated successfully.");
			
			status.setMessage("Book updated successfully.");
			status.setStatusCode(HttpStatus.OK.value());
			
		} catch(Exception e) {
			logger.error("Something went wrong." + e);
			
			status.setMessage("Something went wrong.");
			status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		
		return status;
	}
	
	@Transactional
	public StatusDTO getBookUsingTitleAuthorPartialMatch(String titleSearchTerm, String authorSearchTerm) {
		StatusDTO status = new StatusDTO();
		
		try {
			List<BookDTO> bookList = bookAuthorDAO.searchBookUsingTitleAuthorPartialMatch(titleSearchTerm, authorSearchTerm);
			logger.info("Fetched book list successfully.");
			
			status.setData(bookList);
			status.setMessage("Fetched book list successfully.");
			status.setStatusCode(HttpStatus.OK.value());
			
		} catch(Exception e) {
			logger.error("Something went wrong." + e);
			
			status.setMessage("Something went wrong.");
			status.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		
		return status;
	}
	
	@Transactional
	public Page<BookDTO> getPaginatedBookList(Optional<String> author, Optional<Integer> publishedYear, Optional<AvailabilityStatus> availabilityStatus, Pageable pageable) {
		return bookAuthorDAO.getPaginatedBookList(author, publishedYear, availabilityStatus, pageable);
	}
}
