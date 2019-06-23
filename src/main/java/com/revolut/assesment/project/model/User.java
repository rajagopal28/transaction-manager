package com.revolut.assesment.project.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.sql.Date;

@Builder
@Data
@ToString
@AllArgsConstructor
public class User {
    private Integer id;
    private String firstName;
    private String lastName;
    private String city;
    private String phoneNumber;
    private String email;
    private String gender;
    private String dob;
    private Date timeCreated;
}
