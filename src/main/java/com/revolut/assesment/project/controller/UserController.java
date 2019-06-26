package com.revolut.assesment.project.controller;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.dao.UserDao;
import com.revolut.assesment.project.exception.DatabaseException;
import com.revolut.assesment.project.exception.MoreThanOneRecordFoundException;
import com.revolut.assesment.project.exception.NoDataUpdatedException;
import com.revolut.assesment.project.exception.NoRecordsFoundException;
import com.revolut.assesment.project.model.User;
import com.revolut.assesment.project.vo.MessageVO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users")
public class UserController {
    UserDao userService = new UserDao();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
       try {
           List<User> users = userService.getUsers();
           return Response.status(200).entity(users).build();
       } catch (DatabaseException de) {
           de.printStackTrace();
           return Response.status(500).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_DATABAS_ISSUE).build()).build();
       }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(User user) {
        try {
            userService.addUser(user);
            return Response.status(201).entity(user).build();
        } catch (NoDataUpdatedException nde) {
            nde.printStackTrace();;
            return Response.status(304).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_RECORD_NOT_CREATED).build()).build();
        } catch(DatabaseException de) {
            de.printStackTrace();
            return Response.status(500).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_DATABAS_ISSUE).build()).build();
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
            return Response.status(500).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_DATABAS_ISSUE).build()).build();
        } catch (MoreThanOneRecordFoundException | NoRecordsFoundException nre) {
            nre.printStackTrace();
            return Response.status(404).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_UNABLE_TO_FIND_USER).build()).build();
        }
    }

}
