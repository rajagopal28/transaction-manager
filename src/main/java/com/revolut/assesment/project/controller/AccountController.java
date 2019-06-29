package com.revolut.assesment.project.controller;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.dao.AccountDao;
import com.revolut.assesment.project.exception.DataValidationException;
import com.revolut.assesment.project.exception.NoRecordsFoundException;
import com.revolut.assesment.project.model.Account;
import com.revolut.assesment.project.model.User;
import com.revolut.assesment.project.vo.MessageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users/{user_id}/accounts")
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    AccountDao accountDao = new AccountDao();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUserAccounts(@PathParam("user_id") Integer userId) {
        try {
            List<Account> accounts = accountDao.getAccounts(User.builder().id(userId).build());
            return Response.status(200).entity(accounts).build();
        } catch (Exception de) {
            logger.error(de.getMessage(), de);
            return Response.status(500).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_GENERIC_MESSAGE).build()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(@PathParam("user_id") Integer userId, Account account) {
        try {
            account.setUser(User.builder().id(userId).build());
            account.setTimeCreated(System.currentTimeMillis());
            accountDao.addAccount(account);
            return Response.status(201).entity(account).build();
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
    public Response getAccount(@PathParam("id") Integer id) {
        try {
            Account result = accountDao.getAccount(id);
            return Response.status(200).entity(result).build();
        } catch (NoRecordsFoundException nre) {
            logger.error(nre.getMessage(), nre);
            return Response.status(404).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_UNABLE_TO_FIND_RECORD).build()).build();
        } catch (Exception de) {
            logger.error(de.getMessage(), de);
            return Response.status(500).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_GENERIC_MESSAGE).build()).build();
        }
    }

}
