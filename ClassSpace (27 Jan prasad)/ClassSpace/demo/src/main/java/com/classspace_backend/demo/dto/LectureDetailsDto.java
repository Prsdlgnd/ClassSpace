//package com.classspace_backend.demo.dto;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//public class LectureDetailsDto {
//
//    /* ======================
//       BASIC LECTURE INFO
//       ====================== */
//
//    private Long lectureId;
//    private String subject;
//    private String className;
//    private LocalDate lectureDate;
//
//    // SCHEDULED / CANCELLED / COMPLETED
//    private String status;
//
//    private LocalDateTime startedAt;
//    private LocalDateTime endedAt;
//    private LocalDateTime cancelledAt;
//
//    /* ======================
//       VIEW TYPE CONTROL
//       ====================== */
//
//    // FUTURE or PAST
//    private String lectureType;
//
//    /* ======================
//       FUTURE LECTURE DATA
//       ====================== */
//
//    // Students who declared YES
//    private List<StudentAttendanceDto> declaredStudents;
//
//    /* ======================
//       PAST LECTURE DATA
//       ====================== */
//
//    private List<NoteDto> notes;
//    private List<FeedbackResponseDto> feedback;
//
//    /* ======================
//       CONSTRUCTORS
//       ====================== */
//
//    public LectureDetailsDto() {
//    }
//
//    /* ======================
//       GETTERS & SETTERS
//       ====================== */
//
//    public Long getLectureId() {
//        return lectureId;
//    }
//
//    public void setLectureId(Long lectureId) {
//        this.lectureId = lectureId;
//    }
//
//    public String getSubject() {
//        return subject;
//    }
//
//    public void setSubject(String subject) {
//        this.subject = subject;
//    }
//
//    public String getClassName() {
//        return className;
//    }
//
//    public void setClassName(String className) {
//        this.className = className;
//    }
//
//    public LocalDate getLectureDate() {
//        return lectureDate;
//    }
//
//    public void setLectureDate(LocalDate lectureDate) {
//        this.lectureDate = lectureDate;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public LocalDateTime getStartedAt() {
//        return startedAt;
//    }
//
//    public void setStartedAt(LocalDateTime startedAt) {
//        this.startedAt = startedAt;
//    }
//
//    public LocalDateTime getEndedAt() {
//        return endedAt;
//    }
//
//    public void setEndedAt(LocalDateTime endedAt) {
//        this.endedAt = endedAt;
//    }
//
//    public LocalDateTime getCancelledAt() {
//        return cancelledAt;
//    }
//
//    public void setCancelledAt(LocalDateTime cancelledAt) {
//        this.cancelledAt = cancelledAt;
//    }
//
//    public String getLectureType() {
//        return lectureType;
//    }
//
//    public void setLectureType(String lectureType) {
//        this.lectureType = lectureType;
//    }
//
//    public List<StudentAttendanceDto> getDeclaredStudents() {
//        return declaredStudents;
//    }
//
//    public void setDeclaredStudents(List<StudentAttendanceDto> declaredStudents) {
//        this.declaredStudents = declaredStudents;
//    }
//
//    public List<NoteDto> getNotes() {
//        return notes;
//    }
//
//    public void setNotes(List<NoteDto> notes) {
//        this.notes = notes;
//    }
//
//    public List<FeedbackResponseDto> getFeedback() {
//        return feedback;
//    }
//
//    public void setFeedback(List<FeedbackResponseDto> feedback) {
//        this.feedback = feedback;
//    }
//}
