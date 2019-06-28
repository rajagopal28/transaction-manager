package com.revolut.assesment.project.model;

import com.revolut.assesment.project.constants.ApplicationConstants.AccountType;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String accountNumber;

    @ManyToOne
    private User user;
    private AccountType accountType;
    private String currency;
    private double balance;
    private long timeCreated;
}

