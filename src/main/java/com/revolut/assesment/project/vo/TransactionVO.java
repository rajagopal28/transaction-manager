package com.revolut.assesment.project.vo;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionVO {
    private Integer fromAccountId;
    private Integer toAccountId;
    private double amount;
    private String currency = "";
    private ApplicationConstants.TransactionType transactionType;

    public Transaction getTransaction() {
        return Transaction.builder().amount(amount).transactionType(transactionType).currency(currency).build();
    }
}
