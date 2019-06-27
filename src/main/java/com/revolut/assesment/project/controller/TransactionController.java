package com.revolut.assesment.project.controller;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.dao.TransactionDao;
import com.revolut.assesment.project.exception.*;
import com.revolut.assesment.project.model.Account;
import com.revolut.assesment.project.model.Transaction;
import com.revolut.assesment.project.vo.MessageVO;
import com.revolut.assesment.project.vo.TransactionVO;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users/{user_id}/accounts/{account_id}/transactions")
public class TransactionController {
    TransactionDao transactionDao = new TransactionDao();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAccountTransactions(@PathParam("user_id") Integer userId, @PathParam("account_id") Integer accountId) {
        try {
            List<Transaction> accounts = transactionDao.getTransactions(Account.builder().id(accountId).build());
            return Response.status(200).entity(accounts).build();
        } catch (DatabaseException de) {
            de.printStackTrace();
            return Response.status(500).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_DATABASE_ISSUE).build()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTransaction(@PathParam("user_id") Integer userId, @PathParam("account_id") Integer accountId, TransactionVO transactionVO) {
        try {
            transactionVO.setToAccountId(accountId);
            Transaction transaction = transactionDao.transact(transactionVO);
            return Response.status(201).entity(transaction).build();
        } catch (CurrencyConversionNotSupportedException ccne) {
            ccne.printStackTrace();;
            return Response.status(304).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_CURRENCY_CONVERSION_NOT_DONE).build()).build();
        } catch(InsufficientBalanceException ibe) {
            ibe.printStackTrace();
            return Response.status(304).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_INSUFFICIENT_BALANCE).build()).build();
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransaction(@PathParam("user_id") Integer userId, @PathParam("account_id") Integer accountId,@PathParam("id") Integer id) {
        try {
            Transaction result = transactionDao.getTransaction(id);
            return Response.status(200).entity(result).build();
        } catch (DatabaseException de) {
            de.printStackTrace();
            return Response.status(500).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_DATABASE_ISSUE).build()).build();
        } catch (NoRecordsFoundException nre) {
            nre.printStackTrace();
            return Response.status(404).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_UNABLE_TO_FIND_USER).build()).build();
        }
    }
}
