package com.dgu.server.service;

import com.dgu.server.model.entities.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface ForgotPwdService {
    void updateResetPasswordToken(String token, String email) throws UsernameNotFoundException;
    User getByResetPasswordToken(String token);
    void sendEmail(String token, String recipientEmail) throws MessagingException, UnsupportedEncodingException;
}
