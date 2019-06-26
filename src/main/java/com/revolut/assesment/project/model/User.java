package com.revolut.assesment.project.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
public class User implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String city;
    private String gender;
    private String dob;
}
