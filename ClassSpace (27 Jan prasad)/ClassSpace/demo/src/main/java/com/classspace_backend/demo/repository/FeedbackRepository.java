package com.classspace_backend.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.classspace_backend.demo.entity.Feedback;

@Repository
public interface FeedbackRepository
        extends JpaRepository<Feedback, Long> {

    @Query("""
        SELECT f
        FROM Feedback f
        WHERE f.lecture.lectureId = :lectureId
        AND f.isValid = true
    """)
    List<Feedback> findValidFeedback(Long lectureId);
}

