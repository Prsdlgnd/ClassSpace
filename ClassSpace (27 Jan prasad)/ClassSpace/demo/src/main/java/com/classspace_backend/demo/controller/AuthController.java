package com.classspace_backend.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.classspace_backend.demo.dto.ChangePasswordDto;
import com.classspace_backend.demo.dto.LoginRequestDto;
import com.classspace_backend.demo.dto.RegisterSendOtpDto;
import com.classspace_backend.demo.dto.RegisterVerifyOtpDto;
import com.classspace_backend.demo.dto.SendOtpDto;
import com.classspace_backend.demo.dto.VerifyOtpDto;
import com.classspace_backend.demo.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
@CrossOrigin(
    origins = "http://localhost:5173",
    allowCredentials = "true"
)
public class AuthController {

    @Autowired
    private AuthService authService;
    

    
    
    @PostMapping("/register/send-otp")
    public ResponseEntity<?> sendRegisterOtp(
            @RequestBody RegisterSendOtpDto dto) {

        return authService.sendRegisterOtp(dto);
    }

    @PostMapping("/register/verify-otp")
    public ResponseEntity<?> verifyRegisterOtp(
            @RequestBody RegisterVerifyOtpDto dto,
            HttpServletRequest request) {

        return authService.verifyRegisterOtp(dto, request);
    }


    
    
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordDto dto,
            HttpServletRequest request) {

        return authService.changePassword(dto, request);
    }

    
    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpDto dto) {
        return authService.verifyOtp(dto);
    }


    @PostMapping("/forgot-password/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody SendOtpDto dto) {
        return authService.sendOtp(dto);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto dto,
                                   HttpServletRequest request) {

        return authService.login(dto, request);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok("Logged out");
    }
    
    
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("USER_ID") == null) {
            return ResponseEntity.status(401).body("NOT_LOGGED_IN");
        }

        return ResponseEntity.ok(
            Map.of(
                "userId", session.getAttribute("USER_ID"),
                "role", session.getAttribute("ROLE")
            )
        );
    }
//    
//    @PostMapping("/forgot-password")
//    public ResponseEntity<?> forgotPassword(
//            @RequestBody ForgotPasswordDto dto) {
//
//        return authService.forgotPassword(dto);
//    }

    
    

    
}

