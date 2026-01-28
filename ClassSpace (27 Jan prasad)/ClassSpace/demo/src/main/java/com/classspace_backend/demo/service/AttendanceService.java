package com.classspace_backend.demo.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.classspace_backend.demo.entity.*;
import com.classspace_backend.demo.repository.*;
import com.classspace_backend.demo.util.IntegrityCalculator;
import com.classspace_backend.demo.exception.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Iterator;

@Service
public class AttendanceService {

    private final AttendanceDeclaredRepository declaredRepo;
    private final AttendanceActualRepository actualRepo;
    private final IntegrityScoreRepository integrityRepo;
    private final LectureRepository lectureRepo;
    private final UserRepository userRepo;

    public AttendanceService(
            AttendanceDeclaredRepository declaredRepo,
            AttendanceActualRepository actualRepo,
            IntegrityScoreRepository integrityRepo,
            LectureRepository lectureRepo,
            UserRepository userRepo
    ) {
        this.declaredRepo = declaredRepo;
        this.actualRepo = actualRepo;
        this.integrityRepo = integrityRepo;
        this.lectureRepo = lectureRepo;
        this.userRepo = userRepo;
    }

    // ===============================
    // 1️⃣ STUDENT POLL
    // ===============================
    public void declareAttendance(Long lectureId, Long studentId, DeclaredStatus status) {

        Lecture lecture = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found"));

        LocalDateTime lectureStart = LocalDateTime.of(
                lecture.getLectureDate(),
                lecture.getTimetable().getStartTime()
        );

        if (LocalDateTime.now().isAfter(lectureStart.minusHours(1))) {
            throw new InvalidOperationException("Attendance poll closed");
        }

        AttendanceDeclared ad = declaredRepo
                .findByLecture_LectureIdAndStudent_UserId(lectureId, studentId)
                .orElse(new AttendanceDeclared());

        ad.setLecture(lecture);
        ad.setStudent(userRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found")));
        ad.setDeclaredStatus(status);

        declaredRepo.save(ad);
    }

    // =====================================
    // 2️⃣ COORDINATOR UPLOAD (API / Excel)
    // =====================================
    @Transactional
    public void uploadActual(Long lectureId, Long studentId, ActualStatus actual) {

        Lecture lecture = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found"));

        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // ---------- SAFE-GUARD: avoid duplicate attendance ----------
        final boolean[] isNewAttendance = {false};

        AttendanceActual aa = actualRepo
                .findByLecture_LectureIdAndStudent_UserId(lectureId, studentId)
                .orElseGet(() -> {
                    AttendanceActual newAa = new AttendanceActual();
                    newAa.setLecture(lecture);
                    newAa.setStudent(student);
                    isNewAttendance[0] = true;
                    return newAa;
                });

        aa.setActualStatus(actual);
        actualRepo.save(aa);

        // ---------- DECLARED STATUS ----------
        DeclaredStatus declared = declaredRepo
                .findByLecture_LectureIdAndStudent_UserId(lectureId, studentId)
                .map(AttendanceDeclared::getDeclaredStatus)
                .orElse(DeclaredStatus.YES);

        // ---------- INTEGRITY SCORE ----------
        IntegrityScore score = integrityRepo
                .findByStudent_UserId(studentId)
                .orElseGet(() -> {
                    IntegrityScore s = new IntegrityScore();
                    s.setStudent(student);
                    s.setTotalLectures(0);
                    s.setHonestCount(0);
                    s.setDishonestCount(0);
                    s.setIntegrityPercentage(BigDecimal.valueOf(100));
                    return s;
                });

        // 🔒 SAFE-GUARD: update integrity ONLY once per lecture
        if (isNewAttendance[0]) {
            score.setTotalLectures(score.getTotalLectures() + 1);

            if (IntegrityCalculator.isHonest(declared, actual)) {
                score.setHonestCount(score.getHonestCount() + 1);
            } else {
                score.setDishonestCount(score.getDishonestCount() + 1);
            }

            BigDecimal percentage = BigDecimal.valueOf(
                    (score.getHonestCount() * 100.0) / score.getTotalLectures()
            );
            score.setIntegrityPercentage(percentage);
        }

        integrityRepo.save(score);
    }

    // ===============================
    // 3️⃣ GET INTEGRITY SCORE
    // ===============================
    public IntegrityScore getIntegrityScore(Long studentId) {
        return integrityRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Integrity score not found for studentId: " + studentId
                ));
    }

    // ===============================
    // 4️⃣ EXCEL UPLOAD
    // ===============================
    @Transactional
    public void uploadActualFromExcel(MultipartFile file) throws Exception {

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            while (rows.hasNext()) {
                Row row = rows.next();

                // skip header
                if (row.getRowNum() == 0) continue;

                Cell lectureCell = row.getCell(0);
                Cell studentCell = row.getCell(1);
                Cell statusCell = row.getCell(2);

                if (lectureCell == null || studentCell == null || statusCell == null) continue;

                Long lectureId = (long) lectureCell.getNumericCellValue();
                Long studentId = (long) studentCell.getNumericCellValue();
                ActualStatus status =
                        ActualStatus.valueOf(statusCell.getStringCellValue().toUpperCase());

                uploadActual(lectureId, studentId, status);
            }
        } catch (Exception e) {
            throw new Exception("Error reading Excel file: " + e.getMessage());
        }
    }
}
