package com.revolut.assesment.project.dao;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.exception.DataValidationException;
import com.revolut.assesment.project.exception.NoRecordsFoundException;
import com.revolut.assesment.project.model.Account;
import com.revolut.assesment.project.model.User;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class AccountDao {
    private EntityManager em;

    public AccountDao() {
        em = Persistence.createEntityManagerFactory(ApplicationConstants.SQLITE_DB_NAME).createEntityManager();
    }

    public List<Account> getAccounts(User user) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Account> query = cb.createQuery(Account.class);
        Root<Account> sm = query.from(Account.class);

        ParameterExpression<User> p = cb.parameter(User.class);
        query = query.select(sm).where(cb.equal(sm.get("user"), p));

        TypedQuery<Account> tQuery = em.createQuery(query);
        tQuery.setParameter(p, user);
        return tQuery.getResultList();
    }

    public void addAccount(Account account) {
        validateAccount(account);
        em.getTransaction().begin();
        em.persist(account);
        em.getTransaction().commit();
    }

    public Account getAccount(Integer accountId) {
        final Account account = em.find(Account.class, accountId);
        if(account == null) {
            throw new NoRecordsFoundException();
        }
        return account;
    }

    private void validateAccount(Account account) {
        List<String> failedFields = new ArrayList<>();
        if(account.getCurrency() == null) {
            failedFields.add("currency");
        }
        if(account.getBalance() < 0) {
            failedFields.add("balance");
        }
        if(account.getAccountType() == null) {
            failedFields.add("accountType");
        }
        if(account.getAccountNumber() == null || account.getAccountNumber().trim().isEmpty()) {
            failedFields.add("accountNumber");
        }
        if(!failedFields.isEmpty()) {
            throw new DataValidationException(failedFields.toString());
        }
    }

}
