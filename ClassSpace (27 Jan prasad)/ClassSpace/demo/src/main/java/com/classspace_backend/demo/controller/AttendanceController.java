package com.classspace_backend.demo.controller;

import com.classspace_backend.demo.dto.AttendanceDTO;
import com.classspace_backend.demo.entity.ActualStatus;
import com.classspace_backend.demo.entity.DeclaredStatus;
import com.classspace_backend.demo.entity.IntegrityScore;
import com.classspace_backend.demo.service.AttendanceService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    // ==============================
    // 1️⃣ STUDENT ATTENDANCE POLL
    // ==============================
    @PostMapping("/declare")
    public ResponseEntity<String> declare(@RequestBody AttendanceDTO dto) {
        try {
            DeclaredStatus status = DeclaredStatus.valueOf(dto.status.toUpperCase());
            service.declareAttendance(dto.lectureId, dto.studentId, status);
            return ResponseEntity.ok("Attendance declared successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status. Must be YES or NO");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ==============================
    // 2️⃣ COORDINATOR UPLOAD ACTUAL ATTENDANCE (single student)
    // ==============================
    @PostMapping("/actual")
    public ResponseEntity<String> upload(@RequestBody AttendanceDTO dto) {
        try {
            ActualStatus status = ActualStatus.valueOf(dto.status.toUpperCase());
            service.uploadActual(dto.lectureId, dto.studentId, status);
            return ResponseEntity.ok("Actual attendance uploaded successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status. Must be PRESENT or ABSENT");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ==============================
    // 3️⃣ COORDINATOR UPLOAD ACTUAL ATTENDANCE (Excel file)
    // ==============================
    @PostMapping("/actual/upload")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            service.uploadActualFromExcel(file);
            return ResponseEntity.ok("Excel uploaded and attendance updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process Excel file: " + e.getMessage());
        }
    }

    // ==============================
    // 4️⃣ GET INTEGRITY SCORE
    // ==============================
    @GetMapping("/integrity/{studentId}")
    public ResponseEntity<?> getIntegrity(@PathVariable Long studentId) {
        try {
            IntegrityScore score = service.getIntegrityScore(studentId);
            return ResponseEntity.ok(score);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
