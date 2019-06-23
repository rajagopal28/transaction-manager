package com.revolut.assesment.project.model;

import lombok.*;



@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String city;
    private String gender;
    private String dob;
}
