package com.spry.libraryManagement.DAO;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.spry.libraryManagement.Model.Author;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class AuthorDAO {
	@PersistenceContext
	private EntityManager entityManager;
	
	public Author getAuthorByName(String authorName) {
		String hql = "FROM Author a WHERE a.name = :name";
		
		Query query = entityManager.createQuery(hql, Author.class);
		query.setParameter("name", authorName);
		
		List<Author> result = query.getResultList();
		
		return result.isEmpty() ? null : result.get(0);
	}
	
	public int insertAuthor(Author author) {		
		entityManager.persist(author);
		
		return author.getId();
	}
	
	public String getAuthorNameByBookId(Integer bookId) {
		String sql = "SELECT a.Name AS name FROM "
				+ "Book_Author ba "
				+ "JOIN Book b ON b.Id = ba.Book_Id "
				+ "JOIN Author a ON a.Id = ba.Author_Id "
				+ "WHERE b.id = :bookId";
		
		Query query = entityManager.createNamedQuery(sql);
		query.setParameter("bookId", bookId);
		
		List<String> result = query.getResultList();
		
		return result.isEmpty() ? null : result.get(0);
	}
	
	public Author getAuthorByBookId(Integer bookId) {
		String sql = "SELECT a.id AS Id, a.Name AS name FROM "
				+ "Book_Author ba "
				+ "JOIN Book b ON b.Id = ba.Book_Id "
				+ "JOIN Author a ON a.Id = ba.Author_Id "
				+ "WHERE b.id = :bookId";
		
		Query query = entityManager.createNativeQuery(sql, Author.class);
		query.setParameter("bookId", bookId);
		
		List<Author> result = query.getResultList();
		
		return result.isEmpty() ? null : result.get(0);
				
	}
}
