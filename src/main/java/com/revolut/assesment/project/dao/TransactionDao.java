package com.revolut.assesment.project.dao;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.exception.CurrencyConversionNotSupportedException;
import com.revolut.assesment.project.exception.InsufficientBalanceException;
import com.revolut.assesment.project.model.Account;
import com.revolut.assesment.project.model.Transaction;
import com.revolut.assesment.project.vo.TransactionVO;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.sql.Date;
import java.util.List;

public class TransactionDao {
    private EntityManager em;

    public TransactionDao() {
        em = Persistence.createEntityManagerFactory(ApplicationConstants.SQLITE_DB_NAME).createEntityManager();
    }

    public List<Transaction> getTransactions(Account account) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Transaction> query = cb.createQuery(Transaction.class);
        Root<Transaction> sm = query.from(Transaction.class);

        ParameterExpression<Account> p1 = cb.parameter(Account.class);
        ParameterExpression<Account> p2 = cb.parameter(Account.class);
        Predicate orClause =
                cb.or(cb.equal(sm.<Account>get("fromAccount"), p1),
                        cb.equal(sm.<Account>get("toAccount"), p2));
        query = query.select(sm).where(orClause);

        TypedQuery<Transaction> tQuery = em.createQuery(query);
        tQuery.setParameter(p1, account);
        tQuery.setParameter(p2, account);
        return tQuery.getResultList();
    }

    public Transaction transact(TransactionVO transactionVO) {
        Transaction result = transactionVO.getTransaction();
        switch (transactionVO.getTransactionType()) {
            case TRANSFER:
                result = performAccountTransfer(transactionVO);
                break;
            case CASH_DEPOSIT:
            case CHEQUE_DEPOSIT:
                result = depositTransfer(transactionVO);
                    break;
        }
        return  result;
    }

    private Transaction depositTransfer(TransactionVO transactionVO) {
        Transaction transaction = transactionVO.getTransaction();
        em.getTransaction().begin();
        Account toAccount = em.find(Account.class, transactionVO.getToAccountId());
        toAccount.setBalance(toAccount.getBalance()+transaction.getAmount());

        transaction.setToAccount(toAccount);
        em.persist(transaction);
        em.merge(toAccount);
        em.getTransaction().commit();
        return transaction;
    }

    private Transaction performAccountTransfer(TransactionVO transactionVO) {
        Transaction transaction = transactionVO.getTransaction();
        em.getTransaction().begin();
        Account fromAccount = em.find(Account.class, transactionVO.getFromAccountId());
        Account toAccount = em.find(Account.class, transactionVO.getToAccountId());

        if(!fromAccount.getCurrency().equalsIgnoreCase(toAccount.getCurrency())
                || !fromAccount.getCurrency().equalsIgnoreCase(transactionVO.getCurrency())) {
            em.getTransaction().commit();
            throw new CurrencyConversionNotSupportedException();
        }
        if(transactionVO.getAmount() > fromAccount.getBalance()) {
            em.getTransaction().commit();
            throw new InsufficientBalanceException();
        }
        transaction.setTimeCreated(new Date(System.currentTimeMillis()));
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        fromAccount.setBalance(fromAccount.getBalance()-transactionVO.getAmount());
        toAccount.setBalance(toAccount.getBalance()+transactionVO.getAmount());
        em.persist(transaction);
        em.merge(fromAccount);
        em.merge(toAccount);
        em.getTransaction().commit();
        return transaction;
    }

    public Transaction getTransaction(Integer transactionId) {
        return em.find(Transaction.class, transactionId);
    }


}
