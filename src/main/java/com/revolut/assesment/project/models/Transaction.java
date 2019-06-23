package com.revolut.assesment.project.models;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@Builder
@ToString
public class Transaction {
    private int id;
    private int fromAccount;
    private int toAccount;
    private Date timeCreated;
    private double amount;
    private String currency;
}
