package com.revolut.assesment.project.models;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@Builder
@ToString
public class Transaction {
    private Integer id;
    private Integer fromAccount;
    private Integer toAccount;
    private Date timeCreated;
    private double amount;
    private String currency;
}
