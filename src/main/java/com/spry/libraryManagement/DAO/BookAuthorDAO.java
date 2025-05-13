package com.spry.libraryManagement.DAO;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.spry.libraryManagement.DTO.BookDTO;
import com.spry.libraryManagement.Model.AvailabilityStatus;
import com.spry.libraryManagement.Model.BookAuthor;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class BookAuthorDAO {
	@PersistenceContext
	private EntityManager entityManager;
	
	public void addBookAuthorEntry(BookAuthor bookAuthor) {
		entityManager.persist(bookAuthor);
	}
	
	public void deleteEntryByBookId(Integer bookId) {
		String hql = "DELETE FROM BookAuthor ba WHERE ba.bookId = :bookId";
		
		Query query = entityManager.createQuery(hql);
		query.setParameter("bookId", bookId);
		
		query.executeUpdate();
	}
	
	public void deleteEntryByBookIdAuthorId(Integer bookId, Integer authorId) {
		String hql = "DELETE FROM BookAuthor ba WHERE ba.bookId = :bookId AND ba.authorId = :authorId";
		
		Query query = entityManager.createQuery(hql);
		query.setParameter("bookId", bookId);
		query.setParameter("authorId", authorId);
		
		query.executeUpdate();
	}
	
	public List<BookDTO> getAllBooks() {
		String sql = "SELECT b.id AS bookId, b.Title AS bookTitle, b.ISBN AS isbn, a.Name AS authorName, b.Published_Year AS publishedYear, b.Availability_Status AS availabilityStatus "
				+ "FROM Book_Author ba "
				+ "JOIN Author a ON a.Id = ba.Author_Id "
				+ "JOIN Book b ON b.Id = ba.Book_Id";
		
		List<Object[]> resultList = entityManager.createNativeQuery(sql).getResultList();
		List<BookDTO> bookDetailsList = new ArrayList<BookDTO>();
		
		for(Object[] result : resultList) {
			Integer bookId = (Integer) result[0];
			String bookTitle = (String) result[1];
			String isbn = (String) result[2];
			String authorName = (String) result[3];
			Date sqlDate = (Date) result[4];
	        
			Integer publishedYear = sqlDate.toLocalDate().getYear();
			
			AvailabilityStatus availabilityStatus = AvailabilityStatus.valueOf((String) result[5]);
			
			bookDetailsList.add(new BookDTO(bookId, bookTitle, isbn, publishedYear, authorName, availabilityStatus));
		}
		
		return bookDetailsList.isEmpty() ? null : bookDetailsList;
	}
	
	public List<BookDTO> searchBookUsingTitleAuthorPartialMatch(String titleSearchTerm, String authorSearchTerm) {
		String sql = "SELECT b.id AS bookId, b.Title AS bookTitle, b.ISBN AS isbn, a.Name AS authorName, b.Published_Year AS publishedYear, b.Availability_Status AS availabilityStatus "
				+ "FROM Book_Author ba "
				+ "JOIN Author a ON a.Id = ba.Author_Id "
				+ "JOIN Book b ON b.Id = ba.Book_Id ";
		
		String titleSearchCondition = "b.Title LIKE CONCAT('%', :titleSearchTerm, '%') ";
		String authorSearchCondition = "a.Name LIKE CONCAT('%', :authorSearchTerm, '%') ";
		
		if((titleSearchTerm != null && titleSearchTerm != "") && (authorSearchTerm != null && authorSearchTerm != "")) {
			sql += "WHERE " + titleSearchCondition + " OR " + authorSearchCondition;
			
		} else if(titleSearchTerm != null && titleSearchTerm != "") {
			sql += "WHERE " + titleSearchCondition;
			
		} else if(authorSearchTerm != null && authorSearchTerm != "") {
			sql += "WHERE " + authorSearchCondition;
		}
		
		Query query = entityManager.createNativeQuery(sql);
		if(titleSearchTerm != null && titleSearchTerm != "") query.setParameter("titleSearchTerm", titleSearchTerm);
		if(authorSearchTerm != null && authorSearchTerm != "") query.setParameter("authorSearchTerm", authorSearchTerm);
		
		List<Object[]> resultList = query.getResultList();
		List<BookDTO> bookDetailsList = new ArrayList<BookDTO>();
		
		for(Object[] result : resultList) {
			Integer bookId = (Integer) result[0];
			String bookTitle = (String) result[1];
			String isbn = (String) result[2];
			String authorName = (String) result[3];
			Date sqlDate = (Date) result[4];
	        
			Integer publishedYear = sqlDate.toLocalDate().getYear();
			
			AvailabilityStatus availabilityStatus = AvailabilityStatus.valueOf((String) result[5]);
			
			bookDetailsList.add(new BookDTO(bookId, bookTitle, isbn, publishedYear, authorName, availabilityStatus));
		}
		
		return bookDetailsList.isEmpty() ? null : bookDetailsList;
	}
	
	public Page<BookDTO> getPaginatedBookList(Optional<String> author, Optional<Integer> publishedYear, Optional<AvailabilityStatus> availabilityStatus, Pageable pageable) {
		StringBuilder sql = new StringBuilder("SELECT b.id AS bookId, b.Title AS bookTitle, b.ISBN AS isbn, a.Name AS authorName, b.Published_Year AS publishedYear, b.Availability_Status AS availabilityStatus "
				+ "FROM Book_Author ba "
				+ "JOIN Author a ON a.Id = ba.Author_Id "
				+ "JOIN Book b ON b.Id = ba.Book_Id "
				+ "WHERE 1 = 1 ");
		
		if(author.isPresent()) {
			sql.append("AND a.Name = :author ");
		}
		
		if(publishedYear.isPresent()) {
			sql.append("AND b.Published_Year = :publishedYear ");
		}
		
		if(availabilityStatus.isPresent()) {
			sql.append("AND b.Availability_Status = :availabilityStatus ");
		}
		
		Query query = entityManager.createNativeQuery(sql.toString());
		author.ifPresent(a -> query.setParameter("author", a));
		publishedYear.ifPresent(p -> query.setParameter("publishedYear", p));
		availabilityStatus.ifPresent(as -> query.setParameter("availabilityStatus", as.name()));
		
		int total = query.getResultList().size();
		
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());
		
		List<Object[]> results = query.getResultList();

	    List<BookDTO> bookList = results.stream().map(row -> new BookDTO(
	    		(Integer) row[0],
	    		(String) row[1],
	    		(String) row[2],
	    		((Date) row[4]).toLocalDate().getYear(),
	    		(String) row[3],
	    		AvailabilityStatus.valueOf((String) row[5]))).toList();
		
		return new PageImpl<>(bookList, pageable, total);
	}
}
