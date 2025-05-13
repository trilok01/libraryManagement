package com.spry.libraryManagement.DTO;

public class StatusDTO {
	private String message;
	private Integer statusCode;
	private Object data;
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Integer getStatusCode() {
		return statusCode;
	}
	
	public void setStatusCode(Integer status) {
		this.statusCode = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
