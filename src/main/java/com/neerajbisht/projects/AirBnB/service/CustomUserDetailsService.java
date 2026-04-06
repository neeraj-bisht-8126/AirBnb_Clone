package com.neerajbisht.projects.AirBnB.service;

import com.neerajbisht.projects.AirBnB.dto.SignupRequestDTO;
import com.neerajbisht.projects.AirBnB.dto.SignupResponseDTO;
import com.neerajbisht.projects.AirBnB.entity.User;
import com.neerajbisht.projects.AirBnB.entity.enums.Role;
import com.neerajbisht.projects.AirBnB.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("user not found with this username "+username));
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new AuthenticationServiceException("User not found with userId."+ userId));
    }

}
