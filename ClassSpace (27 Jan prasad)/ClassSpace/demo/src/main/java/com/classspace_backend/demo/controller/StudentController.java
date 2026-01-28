package com.classspace_backend.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.classspace_backend.demo.dto.StudentProfileDto;
import com.classspace_backend.demo.service.StudentService;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentController {

	
	@Autowired
	private StudentService studentService; 
	
//    @GetMapping("/test")
//    public String testApi() {
//        return "Backend is working!";
//    }

	    

		@GetMapping("/profile/{studentId}")
		public StudentProfileDto getProfile(@PathVariable Long studentId) {
		    return studentService.getProfile(studentId);
		}

	

}



