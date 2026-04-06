package com.neerajbisht.projects.AirBnB.service;

import com.neerajbisht.projects.AirBnB.dto.*;
import com.neerajbisht.projects.AirBnB.entity.User;
import com.neerajbisht.projects.AirBnB.entity.enums.Role;
import com.neerajbisht.projects.AirBnB.exception.ResourceNotFoundException;
import com.neerajbisht.projects.AirBnB.repository.UserRepository;
import com.neerajbisht.projects.AirBnB.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;


    public String[] login(LoginRequestDTO loginRequest){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));

        User user = (User) authentication.getPrincipal();

        if(user==null) throw new ResourceNotFoundException("Invalid user! User Not Found.");

        String[] arr = new String[2];
        arr[0] = jwtService.generateAccessToken(user);
        arr[1] = jwtService.generateRefreshToken(user);

        return arr;
    }

    public SignupResponseDTO signup(SignupRequestDTO signupRequestDTO){
        log.info("signup the user with user details {}", signupRequestDTO);
        User user = userRepository.findByEmail(signupRequestDTO.getEmail()).orElse(null);
        if(user!=null) throw new RuntimeException("user already exists with email "+signupRequestDTO.getEmail());
        user = modelMapper.map(signupRequestDTO, User.class);
        user.setPassword(passwordEncoder.encode(signupRequestDTO.getPassword()));
        user.setRoles(Set.of(Role.GUEST));
         user = userRepository.save(user);
         log.info("user is saved successfully {}",user);
         return modelMapper.map(user, SignupResponseDTO.class);
    }

    public String refreshToken(String refreshToken){
        Long userId = jwtService.getUserIdFromToken(refreshToken);

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("Invalid userId from token "+ userId));
        return jwtService.generateAccessToken(user);
    }

}
