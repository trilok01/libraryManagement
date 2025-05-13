package com.spry.libraryManagement.Controller;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {
	@GetMapping("hello")
	public ResponseEntity<String> helloTest() {
		String message = "Hello from the server...";
		
		return new ResponseEntity<String>(message, HttpStatusCode.valueOf(200));
	}
}
