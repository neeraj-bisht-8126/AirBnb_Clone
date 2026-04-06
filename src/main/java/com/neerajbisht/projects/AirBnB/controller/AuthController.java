package com.neerajbisht.projects.AirBnB.controller;

import com.neerajbisht.projects.AirBnB.dto.LoginRequestDTO;
import com.neerajbisht.projects.AirBnB.dto.LoginResponseDTO;
import com.neerajbisht.projects.AirBnB.dto.SignupRequestDTO;
import com.neerajbisht.projects.AirBnB.dto.SignupResponseDTO;
import com.neerajbisht.projects.AirBnB.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody SignupRequestDTO signupRequestDTO){
        return new ResponseEntity<>(
                authService.signup(signupRequestDTO),
                HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request, HttpServletResponse response){
        String[] tokens = authService.login(loginRequestDTO);

        Cookie cookie = new Cookie("refreshToken", tokens[1]);
        cookie.setHttpOnly(true);

        response.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponseDTO(tokens[0]));

    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(HttpServletRequest request){
       String refreshToken = Arrays.stream(request.getCookies()).filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(()-> new AuthenticationServiceException("Refresh Token not found inside of cookies.") {
                });
       String newAccessToken = authService.refreshToken(refreshToken);
       return ResponseEntity.ok(
               new LoginResponseDTO(newAccessToken));
    }

    //Profile APIs
    //Dashboard APIs

}
