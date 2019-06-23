package com.revolut.assesment.project.model;

import org.junit.Assert;
import org.junit.Test;

public class UserTest {

    @Test
    public void userBuilderTest() {
        String city1 = "City1";
        String fName1 = "FName1";
        String lName1 = "lName1";
        String email = "email@gmai.com";
        User user = User.builder().city(city1).firstName(fName1).lastName(lName1).email(email).build();
        Assert.assertNotNull(user);
        Assert.assertEquals(city1, user.getCity());
        Assert.assertEquals(fName1, user.getFirstName());
        Assert.assertEquals(lName1, user.getLastName());
        Assert.assertEquals(email, user.getEmail());
    }
}