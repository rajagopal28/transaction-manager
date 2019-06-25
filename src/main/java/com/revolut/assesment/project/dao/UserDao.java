package com.revolut.assesment.project.dao;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.model.User;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.sql.Connection;
import java.util.List;

public class UserDao {

    private EntityManager em = Persistence.createEntityManagerFactory(ApplicationConstants.SQLITE_DB_NAME).createEntityManager();

    public List<User> getUsers(Connection conn) {
        return em.createQuery(ApplicationConstants.SELECT_ALL_QUERY_p1+ User.class.getSimpleName() + ApplicationConstants.SELECT_ALL_QUERY_p2).getResultList();
    }

    public void addUser(User user, Connection conn) {
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
    }

    public User getUsers(User user, Connection conn) {
        return em.find(User.class, user);
    }


}
