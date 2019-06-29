package com.revolut.assesment.project.controller;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.dao.UserDao;
import com.revolut.assesment.project.exception.DataValidationException;
import com.revolut.assesment.project.exception.NoRecordsFoundException;
import com.revolut.assesment.project.model.User;
import com.revolut.assesment.project.vo.MessageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    UserDao userService = new UserDao();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
       try {
           List<User> users = userService.getUsers();
           return Response.status(200).entity(users).build();
       } catch (Exception de) {
           logger.error(de.getMessage(), de);
           return Response.status(500).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_GENERIC_MESSAGE).build()).build();
       }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(User user) {
        try {
            userService.addUser(user);
            return Response.status(201).entity(user).build();
        } catch (DataValidationException nde) {
            logger.error(nde.getMessage(), nde);
            return Response.status(400).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_DATA_VALIDATION_FAILED_WITH+nde.getFieldNames()).build()).build();
        } catch(Exception de) {
            logger.error(de.getMessage(), de);
            return Response.status(500).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_GENERIC_MESSAGE).build()).build();
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("id") Integer id) {
        try {
            User result = userService.getUser(id);
            return Response.status(200).entity(result).build();
        } catch (NoRecordsFoundException nre) {
            logger.error(nre.getMessage(), nre);
            return Response.status(404).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_UNABLE_TO_FIND_RECORD).build()).build();
        }  catch (Exception de) {
            logger.error(de.getMessage(), de);
            return Response.status(500).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_GENERIC_MESSAGE).build()).build();
        }
    }

}
