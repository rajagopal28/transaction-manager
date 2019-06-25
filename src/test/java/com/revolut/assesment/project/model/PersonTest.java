package com.revolut.assesment.project.model;

import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.Assert.*;

public class PersonTest {
    static EntityManagerFactory emf;
    EntityManager em;

    @Test
    public void testPerson() {
        emf = Persistence.createEntityManagerFactory("usersdb");
        em = emf.createEntityManager();
        em.getTransaction().begin();
        //Persist in database
        Person person = new Person();
        person.setName("person2");
        em.persist(person);
        em.getTransaction().commit();

        //Find by id
        Person personDB = em.find(Person.class, person.getId());

        assertEquals(person.getName(), personDB.getName());
    }

}