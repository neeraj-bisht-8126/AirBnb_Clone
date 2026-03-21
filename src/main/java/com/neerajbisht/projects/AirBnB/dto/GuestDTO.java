package com.neerajbisht.projects.AirBnB.dto;

import com.neerajbisht.projects.AirBnB.entity.User;
import com.neerajbisht.projects.AirBnB.entity.enums.Gender;
import lombok.Data;

@Data
public class GuestDTO {

    private Long id;

    private User user;

    private String name;

    private Gender gender;

    private Integer age;
}
