package com.revolut.assesment.project.controller;

import com.revolut.assesment.project.dao.UserDao;
import com.revolut.assesment.project.dao.util.SQLiteConnectionManager;
import com.revolut.assesment.project.exception.DatabaseException;
import com.revolut.assesment.project.exception.MoreThanOneRecordFoundException;
import com.revolut.assesment.project.exception.NoRecordsFoundException;
import com.revolut.assesment.project.model.User;
import com.revolut.assesment.project.service.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

@Path("/users")
public class UserController {
    private UserService userService = new UserService();
    private SQLiteConnectionManager connectionManager = new SQLiteConnectionManager();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
       try {
           List<User> users = userService.getUsers();
           return Response.status(200).entity(users).build();
       } catch (DatabaseException de) {
           de.printStackTrace();
           return Response.status(500).build();
       }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(User user) {
        try {
            boolean result = userService.addUser(user);
            int status = result ? 201 : 304;
            return Response.status(status).entity(user).build();
        } catch (DatabaseException de) {
            de.printStackTrace();
            return Response.status(500).build();
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("id") Integer id) {
        try {
            User result = userService.getUser(User.builder().id(id).build());
            return Response.status(200).entity(result).build();
        } catch (DatabaseException de) {
            de.printStackTrace();
            return Response.status(500).build();
        } catch (MoreThanOneRecordFoundException | NoRecordsFoundException nre) {
            nre.printStackTrace();
            return Response.status(404).build();
        }
    }

}
