package com.revolut.assesment.project.model;

import com.revolut.assesment.project.constants.ApplicationConstants;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Transaction implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Account fromAccount;
    @ManyToOne
    private Account toAccount;
    private Date timeCreated;
    private double amount;
    private String currency;
    private ApplicationConstants.TransactionType transactionType;
}
