package com.spry.libraryManagement.DTO;

import com.spry.libraryManagement.Model.AvailabilityStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class BookDTO {
	private Integer bookId;
	private String bookTitle;
	private String isbn;
	private Integer publishedYear;
	private String authorName;
	@Enumerated(EnumType.STRING)
	private AvailabilityStatus availabilityStatus;
	
	public BookDTO(Integer bookId, String bookTitle, String isbn, Integer publishedYear, String authorName,
			AvailabilityStatus availabilityStatus) {
		super();
		this.bookId = bookId;
		this.bookTitle = bookTitle;
		this.isbn = isbn;
		this.publishedYear = publishedYear;
		this.authorName = authorName;
		this.availabilityStatus = availabilityStatus;
	}

	public Integer getBookId() {
		return bookId;
	}

	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}

	public String getBookTitle() {
		return bookTitle;
	}
	
	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}
	
	public String getIsbn() {
		return isbn;
	}
	
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	
	public Integer getPublishedYear() {
		return publishedYear;
	}
	
	public void setPublishedYear(Integer publishedYear) {
		this.publishedYear = publishedYear;
	}
	
	public String getAuthorName() {
		return authorName;
	}
	
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public AvailabilityStatus getAvailabilityStatus() {
		return availabilityStatus;
	}

	public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
		this.availabilityStatus = availabilityStatus;
	}
}
