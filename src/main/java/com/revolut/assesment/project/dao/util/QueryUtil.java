package com.revolut.assesment.project.dao.util;

import com.revolut.assesment.project.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QueryUtil {

    public static String getFindUserWhereClause(User user, List<Object> params) {
        List<String> conditionals = new ArrayList<>();
        if(user.getId() != null) {
            conditionals.add(" ID = ? ");
            params.add(user.getId());
        }
        if(user.getFirstName() != null) {
            conditionals.add(" FIRST_NAME = ? ");
            params.add(user.getFirstName());
        }
        if(user.getLastName() != null) {
            conditionals.add(" LAST_NAME = ? ");
            params.add(user.getLastName());
        }
        if(user.getEmail() != null) {
            conditionals.add(" EMAIL = ? ");
            params.add(user.getEmail());
        }
        if(user.getPhoneNumber() != null) {
            conditionals.add(" PHONE_NUMBER = ? ");
            params.add(user.getPhoneNumber());
        }
        String whereClause = "";
        if(!conditionals.isEmpty()) {
            whereClause = " WHERE " + conditionals.stream().collect(Collectors.joining(" AND "));
        }
        return whereClause;
    }
}
