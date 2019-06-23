package com.revolut.assesment.project.dao.util;

import com.revolut.assesment.project.models.User;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class QueryUtilTest {

    @Test
    public void testQueryUtilsGetUserWhereClauseScenarios() {
        // empty user
        ArrayList<Object> p1 = new ArrayList<>();
        String q1 = QueryUtil.getFindUserWhereClause(User.builder().build(), p1);
        assertTrue(q1.isEmpty());
        assertTrue(p1.isEmpty());

        p1.clear();
        q1 = QueryUtil.getFindUserWhereClause(User.builder().email("email1").lastName("lastname1").build(), p1);
        assertTrue(!q1.isEmpty());
        assertTrue(!p1.isEmpty());
        assertEquals(" WHERE  LAST_NAME = ?  AND  EMAIL = ? ", q1);
        assertEquals(2, p1.size());
        assertTrue(p1.stream().anyMatch(s -> s.equals("email1")));
        assertTrue(p1.stream().anyMatch(s -> s.equals("lastname1")));

        p1.clear();
        q1 = QueryUtil.getFindUserWhereClause(User.builder().firstName("firstName2").lastName("lastname2").build(), p1);
        assertTrue(!q1.isEmpty());
        assertTrue(!p1.isEmpty());
        assertEquals(" WHERE  FIRST_NAME = ?  AND  LAST_NAME = ? ", q1);
        assertEquals(2, p1.size());
        assertTrue(p1.stream().anyMatch(s -> s.equals("firstName2")));
        assertTrue(p1.stream().anyMatch(s -> s.equals("lastname2")));

        p1.clear();
        q1 = QueryUtil.getFindUserWhereClause(User.builder().id(123).build(), p1);
        assertTrue(!q1.isEmpty());
        assertTrue(!p1.isEmpty());
        assertEquals(" WHERE  ID = ? ", q1);
        assertEquals(1, p1.size());
        assertTrue(p1.stream().anyMatch(s -> s.equals(123)));

        p1.clear();
        q1 = QueryUtil.getFindUserWhereClause(User.builder().phoneNumber("phonenumber3").email("email3").build(), p1);
        assertTrue(!q1.isEmpty());
        assertTrue(!p1.isEmpty());
        assertEquals(" WHERE  EMAIL = ?  AND  PHONE_NUMBER = ? ", q1);
        assertEquals(2, p1.size());
        assertTrue(p1.stream().anyMatch(s -> s.equals("phonenumber3")));
        assertTrue(p1.stream().anyMatch(s -> s.equals("email3")));

    }
}