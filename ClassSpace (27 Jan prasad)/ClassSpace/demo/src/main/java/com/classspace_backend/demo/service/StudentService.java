package com.classspace_backend.demo.service;
import org.springframework.stereotype.Service;

import com.classspace_backend.demo.dto.StudentProfileDto;
import com.classspace_backend.demo.entity.Student;
import com.classspace_backend.demo.entity.User;
import com.classspace_backend.demo.repository.StudentRepository;
import com.classspace_backend.demo.repository.UserRepository;

@Service
public class StudentService {

   private final StudentRepository studentRepository;
   private final UserRepository userRepository;
  
   public StudentService(StudentRepository studentRepository, UserRepository userRepository) {
	super();
	this.studentRepository = studentRepository;
	this.userRepository = userRepository;
   }
   
   public StudentProfileDto  getProfile(Long studentId)
   {
	   Student student = studentRepository.findById(studentId).orElseThrow(()-> new RuntimeException("Student not found"));
	   
	   User user = userRepository.findById(studentId).orElseThrow(()-> new RuntimeException("User not found"));
	   
	   StudentProfileDto dto = new StudentProfileDto();
	   
	   
	   dto.setName(user.getName());
	   dto.setEmail(user.getEmail());
       dto.setPhone(user.getPhone());
       dto.setDob(user.getDob());

       dto.setPrn(student.getPrn());
       dto.setBranch(student.getBranch());
       dto.setDivision(student.getDivision());
       
       return dto;
	   
	   
   }

   
   
   

	
}
