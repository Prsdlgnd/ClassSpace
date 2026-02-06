package com.classspace_backend.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRegisterOtp(String to, String otp, String prn) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ClassSpace <sohampatil363@gmail.com>");
        message.setTo(to);
        message.setSubject(" Verify your email for ClassSpace");

        message.setText(
            "Hi \n\n" +
            "Welcome to ClassSpace!\n\n" +
            "Your username (PRN / Roll No): " + prn + "\n\n" +
            "Your OTP for completing registration is:\n\n" +
            " " + otp + "\n\n" +
            "This OTP is valid for 5 minutes.\n\n" +
            "If you did not initiate this request, please ignore this email.\n\n" +
            "Thanks,\n" +
            "Team ClassSpace"
        );

        mailSender.send(message);
    }


    public void sendForgotPasswordOtp(String to, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ClassSpace <sohampatil363@gmail.com>");
        message.setTo(to);
        message.setSubject(" Reset your ClassSpace password");

        message.setText(
            "Hi \n\n" +
            "We received a request to reset your ClassSpace password.\n\n" +
            "Your OTP for password reset is:\n\n" +
            " " + otp + "\n\n" +
            "This OTP is valid for 5 minutes.\n\n" +
            "If you did not request a password reset, you can safely ignore this email.\n\n" +
            "Regards,\n" +
            "Team ClassSpace"
        );

        mailSender.send(message);
    }
}
