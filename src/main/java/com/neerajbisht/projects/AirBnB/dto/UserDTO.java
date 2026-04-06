package com.neerajbisht.projects.AirBnB.dto;


import com.neerajbisht.projects.AirBnB.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {

    private Long id;

    private String email;

    private String password;

    private String name;

    private Set<Role> roles;

}
