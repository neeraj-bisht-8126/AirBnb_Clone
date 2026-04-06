package com.neerajbisht.projects.AirBnB.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDTO {
    private String name;
    private String email;
    private String password;
}
