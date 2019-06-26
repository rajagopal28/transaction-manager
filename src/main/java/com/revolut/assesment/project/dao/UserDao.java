package com.revolut.assesment.project.dao;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.model.User;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.List;


public class UserDao {
    private EntityManager em;

    public UserDao() {
        System.out.println("Iniitalizing em....");
        em = Persistence.createEntityManagerFactory(ApplicationConstants.SQLITE_DB_NAME).createEntityManager();
    }

    public List<User> getUsers() {
        return em.createQuery(ApplicationConstants.SELECT_ALL_QUERY_p1+ User.class.getSimpleName() + ApplicationConstants.SELECT_ALL_QUERY_p2).getResultList();
    }

    public void addUser(User user) {
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
    }

    public User getUser(Integer id) {
        return em.find(User.class, id);
    }


}
