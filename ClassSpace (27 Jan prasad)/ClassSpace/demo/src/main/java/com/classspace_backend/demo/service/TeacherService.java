package com.classspace_backend.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.classspace_backend.demo.dto.TeacherTimetableDto;
import com.classspace_backend.demo.entity.Announcement;
import com.classspace_backend.demo.entity.Classes;
import com.classspace_backend.demo.entity.Lecture;
import com.classspace_backend.demo.entity.LectureStatus;
import com.classspace_backend.demo.entity.Note;
import com.classspace_backend.demo.entity.User;
import com.classspace_backend.demo.repository.AnnouncementRepository;
import com.classspace_backend.demo.repository.LectureRepository;
import com.classspace_backend.demo.repository.NotesRepository;
import com.classspace_backend.demo.repository.TimetableRepository;
import com.classspace_backend.demo.repository.UserRepository;

@Service
public class TeacherService {

    private final TimetableRepository timetableRepository;
    private final LectureRepository lectureRepository;
    private final NotesRepository notesRepository;
    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    public TeacherService(
            TimetableRepository timetableRepository,
            LectureRepository lectureRepository,
            NotesRepository notesRepository,
            AnnouncementRepository announcementRepository,
            UserRepository userRepository
    ) {
        this.timetableRepository = timetableRepository;
        this.lectureRepository = lectureRepository;
        this.notesRepository = notesRepository;
        this.announcementRepository = announcementRepository;
        this.userRepository = userRepository;
        
    }

    // ==========================
    // 1️⃣ Teacher Dashboard
    // ==========================
    public List<TeacherTimetableDto> getTeacherTimetable(Long teacherId) {

        List<TeacherTimetableDto> timetable =
                timetableRepository.findTeacherTimetable(teacherId);

        timetable.sort((a, b) -> {
            int dayCompare = a.getDay().ordinal() - b.getDay().ordinal();
            if (dayCompare != 0) return dayCompare;
            return a.getStartTime().compareTo(b.getStartTime());
        });

        return timetable;
    }


    private int getDayOrder(String day) {
        return switch (day) {
            case "MON" -> 1;
            case "TUE" -> 2;
            case "WED" -> 3;
            case "THU" -> 4;
            case "FRI" -> 5;
            case "SAT" -> 6;
            default -> 7;
        };
    }

    // ==========================
    // 2️⃣ Create lecture instance (date-wise)
    // ==========================
    public Lecture createLecture(Long timetableId) {
        Lecture lecture = new Lecture();
        lecture.setTimetable(
                timetableRepository.findById(timetableId)
                        .orElseThrow(() -> new RuntimeException("Timetable not found"))
        );
        lecture.setLectureDate(LocalDate.now());
        lecture.setStatus(LectureStatus.SCHEDULED);
        return lectureRepository.save(lecture);
    }

    // ==========================
    // 3️⃣ Cancel lecture
    // ==========================
    public void cancelLecture(Long lectureId, Long teacherId) {

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("Lecture not found"));

        // 1️⃣ Cancel lecture
        lecture.setStatus(LectureStatus.CANCELLED);
        lecture.setCancelledAt(LocalDateTime.now());
        lectureRepository.save(lecture);

        // 2️⃣ Get required entities (IMPORTANT PART)
        User teacher = lecture.getTimetable().getTeacher();          // ✅ User entity
        Classes classEntity = lecture.getTimetable().getClassEntity(); // ✅ Classes entity

        // 3️⃣ Create announcement (ENTITY SET KARO)
        Announcement ann = new Announcement();
        ann.setTitle("Lecture Cancelled");
        ann.setMessage("Today's lecture has been cancelled.");
        ann.setCreatedBy(teacher);        // ✅ ENTITY
        ann.setClassEntity(classEntity);  // ✅ ENTITY

        announcementRepository.save(ann);
    }


    // ==========================
    // 4️⃣ Upload notes (past lecture)
    // ==========================
    public void uploadNotes(
            Long lectureId,
            MultipartFile file,
            Long teacherId
    ) {
        // 1️⃣ Get lecture
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("Lecture not found"));

        // 2️⃣ Get teacher (User entity)
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // 3️⃣ File upload logic (placeholder)
        String driveLink = "https://drive.fake/" + file.getOriginalFilename();

        // 4️⃣ Save note
        Note note = new Note();
        note.setLecture(lecture);
        note.setUploadedBy(teacher);   // ✅ CORRECT
        note.setDriveLink(driveLink);
        note.setVersion(1);

        notesRepository.save(note);
    }

}
