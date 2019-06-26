package com.revolut.assesment.project.dao;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.model.Account;
import com.revolut.assesment.project.model.User;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.List;

public class AccountDao {
    private EntityManager em;

    public AccountDao() {
        em = Persistence.createEntityManagerFactory(ApplicationConstants.SQLITE_DB_NAME).createEntityManager();
    }

    public List<Account> getAccounts(Integer userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Account> query = cb.createQuery(Account.class);
        Root<Account> sm = query.from(Account.class);

        ParameterExpression<Integer> p = cb.parameter(Integer.class);
        query = query.select(sm).where(cb.equal(sm.get("user_id"), p));

        TypedQuery<Account> tQuery = em.createQuery(query);
        tQuery.setParameter(p, userId);
        return tQuery.getResultList();
    }

    public void addAccount(User user, Account account) {
        em.getTransaction().begin();
        account.setUser(em.find(User.class, user.getId()));
        em.persist(account);
        em.getTransaction().commit();
    }

}
