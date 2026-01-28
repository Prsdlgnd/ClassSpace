package com.classspace_backend.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.classspace_backend.demo.dto.TeacherTimetableDto;
import com.classspace_backend.demo.entity.Timetable;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    @Query("""
        SELECT t
        FROM Timetable t
        WHERE t.teacher.userId = :teacherId
        ORDER BY t.day, t.startTime
    """)
    List<Timetable> findByTeacherId(Long teacherId);

    @Query("""
            SELECT new com.classspace_backend.demo.dto.TeacherTimetableDto(
                t.timetableId,
                t.day,
                t.startTime,
                t.endTime,
                t.subject,
                c.className
            )
            FROM Timetable t
            JOIN t.classEntity c
            WHERE t.teacher.userId = :teacherId
        """)
        List<TeacherTimetableDto> findTeacherTimetable(Long teacherId);
}
