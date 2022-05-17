package com.dgu.server.controller;
import com.dgu.server.model.payload.request.*;
import com.dgu.server.model.payload.response.JwtResponse;
import com.dgu.server.model.payload.response.MessageResponse;
import com.dgu.server.repository.RoleRepository;
import com.dgu.server.repository.UserRepository;
import com.dgu.server.applicationConfig.jwt.JwtUtils;
import com.dgu.server.service.impl.ForgotPwdServiceImpl;
import com.dgu.server.service.impl.UserDetailsImpl;
import com.dgu.server.service.impl.UserDetailsServiceImpl;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    ForgotPwdServiceImpl forgotPwdService;
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        if(!userRepository.getById(userDetails.getId()).getApproved())
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not approved!"));
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getTrigramme(),
                userDetails.getEmail(),
                roles));
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        if (userRepository.existsByTrigramme(signUpRequest.getTrigramme())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Trigram is already taken!"));
        }
        // Create new user's account
        new UserDetailsServiceImpl().signUpUser(signUpRequest, encoder, roleRepository, userRepository);
        return ResponseEntity.ok(new MessageResponse("User registered successfully! Please wait for approval!"));
    }
    @PostMapping("/sendemail")
    public ResponseEntity<?> sendEmail(@Valid @RequestBody SendEmailRequest sendEmailRequest){
        if (!userRepository.existsByEmail(sendEmailRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email doesn't exist!"));
        }
        String token = RandomString.make(6);
        forgotPwdService.updateResetPasswordToken(token,sendEmailRequest.getEmail());
        try{
            forgotPwdService.sendEmail(token, sendEmailRequest.getEmail());
        }catch(UnsupportedEncodingException | MessagingException e){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email failed to be sent"));
        }
        return ResponseEntity
                .ok(new MessageResponse("Verification Code has been sent! Please check your inbox!"));
    }

    @PostMapping("/verifycode")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody VerifyCodeRequest verifyCodeRequest){
        try{
           forgotPwdService.getByResetPasswordToken(verifyCodeRequest.getToken()).equals(userRepository.getByEmail(verifyCodeRequest.getEmail()));
        }catch(Exception e){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Code is invalid!"));
        }
        return ResponseEntity
                .ok(new MessageResponse("Code is valid!"));
    }

    @PostMapping("/changepwd")
    public ResponseEntity<?> changePwd(@Valid @RequestBody ChangePwdRequest changePwdRequest){
        if(!userRepository.existsByEmail(changePwdRequest.getEmail()))
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email doesn't exist!"));
        forgotPwdService.updatePassword(userRepository.getByEmail(changePwdRequest.getEmail()),changePwdRequest.getPassword());
        if(userRepository.getByEmail(changePwdRequest.getEmail()).getResetpasswordtoken()!=null)
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Password update failed!"));
        return ResponseEntity
                .ok(new MessageResponse("Success: Password updated!"));
    }
}