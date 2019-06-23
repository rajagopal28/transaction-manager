package com.revolut.assesment.project.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Builder
@Data
@ToString
public class User {
    private Integer id;
    private String firstName;
    private String lastName;
    private String city;
    private String phoneNumber;
    private String email;
    private String gender;
    private Date dateOfBirth;
    private Date timeCreated;
}
