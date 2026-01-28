package com.classspace_backend.demo.dto;

import java.time.LocalTime;

import com.classspace_backend.demo.entity.Day;

public class TeacherTimetableDto {

    private Long timetableId;
    private Day day;
    private LocalTime startTime;
    private LocalTime endTime;
    private String subject;
    private String className;

    public TeacherTimetableDto(
            Long timetableId,
            Day day,
            LocalTime startTime,
            LocalTime endTime,
            String subject,
            String className) {
        this.timetableId = timetableId;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
        this.className = className;
    }

    // getters only (immutable DTO)
    public Long getTimetableId() { return timetableId; }
    public Day getDay() { return day; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getSubject() { return subject; }
    public String getClassName() { return className; }
}
