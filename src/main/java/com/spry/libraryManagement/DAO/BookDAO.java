package com.spry.libraryManagement.DAO;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.spry.libraryManagement.DTO.BookDTO;
import com.spry.libraryManagement.Model.AvailabilityStatus;
import com.spry.libraryManagement.Model.Book;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class BookDAO {
	@PersistenceContext
	private EntityManager entityManager;
	
	public List<Book> getAllBooks() {
		String hql = "FROM Book";
		
		return entityManager.createQuery(hql, Book.class).getResultList();
	}
	
	public String getISBN(String isbn) {
		String hql = "SELECT b.isbn FROM Book b WHERE b.isbn = :isbn";

		Query query = entityManager.createQuery(hql, String.class);
		query.setParameter("isbn", isbn);
		
		List<String> result = query.getResultList();
		
		return result.isEmpty() ? null : (String) result.get(0);
	}
	
	public int addBook(Book book) {
		entityManager.persist(book);
		
		return book.getId();
	}
	
	public Book getBookByName(String bookName) {
		String hql = "FROM Book b WHERE b.title = :title";
		
		Query query = entityManager.createQuery(hql, Book.class);
		query.setParameter("title", bookName);
		
		List<Book> bookResult = query.getResultList();
		
		return bookResult.isEmpty() ? null : bookResult.get(0);
	}
	
	public void deleteBookById(int bookId) {
		String hql = "DELETE FROM Book b WHERE b.id = :bookId";
		
		Query query = entityManager.createQuery(hql);
		query.setParameter("bookId", bookId);
		
		query.executeUpdate();
	}
	
	public void updateBookAvailabilityById(int bookId, AvailabilityStatus availabilityStatus) {
		String hql = "UPDATE Book b SET b.availabilityStatus = :status WHERE b.id = :bookId";
		
		Query query = entityManager.createQuery(hql);
		query.setParameter("bookId", bookId);
		query.setParameter("status", availabilityStatus);
		
		query.executeUpdate();
	}
	
	public Book getBookById(Integer bookId) {
		String hql = "FROM Book b WHERE b.id = :bookId";
		
		Query query = entityManager.createQuery(hql, Book.class);
		query.setParameter("bookId", bookId);
		
		List<Book> bookResult = query.getResultList();
		
		return bookResult.isEmpty() ? null : bookResult.get(0);
	}
	
	public void updateBookDetails(BookDTO bookDTO) {
		String sql = "UPDATE Book SET Title = :bookTitle, ISBN = :isbn, Published_Year = :publishedYear WHERE Id = :bookId";
		
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("bookTitle", bookDTO.getBookTitle());
		query.setParameter("isbn", bookDTO.getIsbn());
		query.setParameter("publishedYear", bookDTO.getPublishedYear());
		query.setParameter("bookId", bookDTO.getBookId());
		
		query.executeUpdate();
	}
}
