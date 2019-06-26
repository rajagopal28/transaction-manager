package com.revolut.assesment.project.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@ToString
@Entity
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
}
