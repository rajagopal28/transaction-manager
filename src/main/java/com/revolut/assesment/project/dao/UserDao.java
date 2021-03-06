package com.revolut.assesment.project.dao;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.exception.DataValidationException;
import com.revolut.assesment.project.exception.NoRecordsFoundException;
import com.revolut.assesment.project.model.User;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;


public class UserDao {
    private EntityManager em;

    public UserDao() {
        em = Persistence.createEntityManagerFactory(ApplicationConstants.SQLITE_DB_NAME).createEntityManager();
    }

    public List<User> getUsers() {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> sm = query.from(User.class);
        query = query.select(sm);

        TypedQuery<User> typedQuery = em.createQuery(query);
        return typedQuery.getResultList();
    }

    public void addUser(User user) {
        validateUser(user);
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
    }

    public User getUser(Integer id) {
        final User user = em.find(User.class, id);
        if(user == null) {
            throw new NoRecordsFoundException();
        }
        return user;
    }

    private void validateUser(User user) {
        List<String> failedFields = new ArrayList<>();
        if(isBlank(user.getCity())) {
            failedFields.add("city");
        }
        if(isBlank(user.getEmail())) {
            failedFields.add("email");
        }
        if(isBlank(user.getFirstName())) {
            failedFields.add("firstName");
        }
        if(isBlank(user.getGender())) {
            failedFields.add("gender");
        }
        if(isBlank(user.getDob())) {
            failedFields.add("dob");
        }
        if(!failedFields.isEmpty()) {
            throw new DataValidationException(failedFields.toString());
        }
    }

    boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }


}
