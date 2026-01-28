package com.classspace_backend.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.classspace_backend.demo.entity.Student;


public interface StudentRepository extends JpaRepository<Student , Long> {
	
	 

	 Optional<Student> findById(Long studentId);
	 Optional<Student> findByPrn(String prn);


}
