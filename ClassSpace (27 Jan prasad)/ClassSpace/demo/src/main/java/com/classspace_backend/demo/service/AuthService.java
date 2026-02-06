package com.classspace_backend.demo.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.classspace_backend.demo.dto.ChangePasswordDto;
import com.classspace_backend.demo.dto.LoginRequestDto;
import com.classspace_backend.demo.dto.RegisterSendOtpDto;
import com.classspace_backend.demo.dto.RegisterVerifyOtpDto;
import com.classspace_backend.demo.dto.SendOtpDto;
import com.classspace_backend.demo.dto.VerifyOtpDto;
import com.classspace_backend.demo.entity.PasswordResetOtp;
import com.classspace_backend.demo.entity.Student;
import com.classspace_backend.demo.entity.User;
import com.classspace_backend.demo.exception.BadRequestException;
import com.classspace_backend.demo.exception.NotFoundException;
import com.classspace_backend.demo.exception.UnauthorizedException;
import com.classspace_backend.demo.repository.PasswordResetOtpRepository;
import com.classspace_backend.demo.repository.RoleRepository;
import com.classspace_backend.demo.repository.StudentRepository;
import com.classspace_backend.demo.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    private PasswordResetOtpRepository otpRepository;

   
    // LOGIN
   
    public ResponseEntity<?> login(LoginRequestDto dto,
                                   HttpServletRequest request) {

        User user;

        //  STUDENT LOGIN (PRN)
        if ("STUDENT".equals(dto.getRole())) {

            Student student = studentRepository.findByPrn(dto.getUsername())
                    .orElseThrow(() ->
                            new UnauthorizedException("Invalid PRN or password"));

            user = userRepository.findById(student.getStudentId())
                    .orElseThrow(() ->
                            new UnauthorizedException("Invalid PRN or password"));
        }
        // TEACHER LOGIN (EMAIL)
        else {
            user = userRepository.findByEmail(dto.getUsername())
                    .orElseThrow(() ->
                            new UnauthorizedException("Invalid email or password"));
        }

        //  PASSWORD CHECK
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String role = user.getRole().getRoleName();

        //  ROLE VALIDATION
        if ("STUDENT".equals(dto.getRole())
                && !(role.equals("STUDENT") || role.equals("COORDINATOR"))) {
            throw new UnauthorizedException("Access denied");
        }

        if ("TEACHER".equals(dto.getRole()) && !role.equals("TEACHER")) {
            throw new UnauthorizedException("Access denied");
        }

        //  CREATE SESSION
        HttpSession session = request.getSession(true);
        session.setAttribute("USER_ID", user.getUserId());
        session.setAttribute("ROLE", role);

        //  FIRST LOGIN → FORCE PASSWORD CHANGE
        if (passwordEncoder.matches(dto.getUsername(), user.getPassword())) {
            return ResponseEntity.status(428)
                    .body("FORCE_PASSWORD_CHANGE");
        }

        return ResponseEntity.ok(role + "_LOGIN_SUCCESS");
    }

    
    // SEND OTP
   
    @Transactional
    public ResponseEntity<?> sendOtp(SendOtpDto dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() ->
                        new NotFoundException("Email not registered"));

        // delete previous OTPs
        otpRepository.deleteByEmail(dto.getEmail());

        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

        PasswordResetOtp entity = new PasswordResetOtp();
        entity.setEmail(dto.getEmail());
        entity.setOtp(otp);
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        entity.setVerified(false);

        otpRepository.save(entity);

        emailService.sendForgotPasswordOtp(dto.getEmail(), otp);

        return ResponseEntity.ok("OTP_SENT");
    }

   
    // VERIFY OTP
   
    public ResponseEntity<?> verifyOtp(VerifyOtpDto dto) {

        PasswordResetOtp otpEntity = otpRepository
                .findTopByEmailOrderByCreatedAtDesc(dto.getEmail())
                .orElseThrow(() ->
                        new BadRequestException("OTP not found"));

        if (otpEntity.isVerified()) {
            throw new BadRequestException("OTP already used");
        }

        if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP expired");
        }

        if (!otpEntity.getOtp().equals(dto.getOtp())) {
            throw new UnauthorizedException("Invalid OTP");
        }

        otpEntity.setVerified(true);
        otpRepository.save(otpEntity);

        return ResponseEntity.ok("OTP_VERIFIED");
    }

    
    // CHANGE PASSWORD (COMMON)
    public ResponseEntity<?> changePassword(ChangePasswordDto dto,
                                            HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        User user;

        // CASE 1: FIRST LOGIN (SESSION)
        if (session != null && session.getAttribute("USER_ID") != null) {

            Long userId = (Long) session.getAttribute("USER_ID");
            user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new NotFoundException("User not found"));
        }
        //  CASE 2: FORGOT PASSWORD (OTP)
        else {
            PasswordResetOtp otpEntity = otpRepository
                    .findTopByEmailOrderByCreatedAtDesc(dto.getEmail())
                    .orElseThrow(() ->
                            new BadRequestException("OTP not found"));

            if (!otpEntity.isVerified()) {
                throw new UnauthorizedException("OTP not verified");
            }

            user = userRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() ->
                            new NotFoundException("User not found"));

            otpRepository.delete(otpEntity); // cleanup
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok("PASSWORD_CHANGED");
    }
    
    @Transactional
    public ResponseEntity<?> sendRegisterOtp(RegisterSendOtpDto dto) {

        //  email already registered
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }

        // delete old OTPs
        otpRepository.deleteByEmail(dto.getEmail());

        String otp = String.valueOf(
            (int)(Math.random() * 900000) + 100000
        );

        PasswordResetOtp entity = new PasswordResetOtp();
        entity.setEmail(dto.getEmail());
        entity.setOtp(otp);
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        entity.setVerified(false);

        otpRepository.save(entity);

        emailService.sendRegisterOtp(dto.getEmail(), otp, dto.getPrn());


        return ResponseEntity.ok("REGISTER_OTP_SENT");
    }
    
    @Transactional
    public ResponseEntity<?> verifyRegisterOtp(
            RegisterVerifyOtpDto dto,
            HttpServletRequest request) {

        PasswordResetOtp otpEntity = otpRepository
            .findTopByEmailOrderByCreatedAtDesc(dto.getEmail())
            .orElseThrow(() ->
                new BadRequestException("OTP not found"));

        if (studentRepository.findByPrn(dto.getPrn()).isPresent()) {
            throw new BadRequestException("PRN already registered");
        }

        if (otpEntity.isVerified()) {
            throw new BadRequestException("OTP already used");
        }

        if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP expired");
        }

        if (!otpEntity.getOtp().equals(dto.getOtp())) {
            throw new BadRequestException("Invalid OTP");
        }

        //  Mark OTP verified
        otpEntity.setVerified(true);
        otpRepository.save(otpEntity);

        //  CREATE USER (temp password)
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getEmail())); // temp password
        user.setRole(
            roleRepository.findByRoleName("STUDENT")
                .orElseThrow(() -> new NotFoundException("Role not found"))
        );
        user.setDob(dto.getDob());
        user.setPhone(dto.getPhone());

        userRepository.save(user);
        userRepository.flush(); //  ensure ID generated

        //  CREATE STUDENT PROFILE
        Student student = new Student();
        student.setUser(user);          // MapsId link
        student.setPrn(dto.getPrn());
        student.setBranch(null);
        student.setDivision(null);

        studentRepository.save(student);

        //  CREATE SESSION (FORCE PASSWORD CHANGE FLOW)
        HttpSession session = request.getSession(true);
        session.setAttribute("USER_ID", user.getUserId());
        session.setAttribute("ROLE", "STUDENT");

        //  Cleanup OTP
        otpRepository.delete(otpEntity);

        //  FORCE USER TO SET PASSWORD
        return ResponseEntity.status(428)
                .body("FORCE_PASSWORD_CHANGE");
    }


    



}
