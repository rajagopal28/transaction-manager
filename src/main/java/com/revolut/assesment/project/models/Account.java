package com.revolut.assesment.project.models;

import com.revolut.assesment.project.constants.ApplicationConstants.AccountType;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@Builder
public class Account {
    private int id;
    private int userId;
    private AccountType accountType;
    private String currency;
    private double balance;
    private Date timeCreated;
}

