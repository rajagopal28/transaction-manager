package com.revolut.assesment.project.controller;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.dao.TransactionDao;
import com.revolut.assesment.project.exception.*;
import com.revolut.assesment.project.model.Account;
import com.revolut.assesment.project.model.Transaction;
import com.revolut.assesment.project.vo.MessageVO;
import com.revolut.assesment.project.vo.TransactionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users/{user_id}/accounts/{account_id}/transactions")
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    TransactionDao transactionDao = new TransactionDao();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAccountTransactions(@PathParam("user_id") Integer userId, @PathParam("account_id") Integer accountId) {
        try {
            List<Transaction> accounts = transactionDao.getTransactions(Account.builder().id(accountId).build());
            return Response.status(200).entity(accounts).build();
        } catch (Exception de) {
            logger.error(de.getMessage(), de);
            return Response.status(500).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_GENERIC_MESSAGE).build()).build();
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
        } catch (NoRecordsFoundException nre) {
            logger.error(nre.getMessage(), nre);
            return Response.status(404).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_UNABLE_TO_FIND_RECORD).build()).build();
        } catch (CurrencyConversionNotSupportedException ccne) {
            logger.error(ccne.getMessage(), ccne);
            return Response.status(400).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_CURRENCY_CONVERSION_NOT_DONE).build()).build();
        } catch(InsufficientBalanceException ibe) {
            logger.error(ibe.getMessage(), ibe);
            return Response.status(400).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_INSUFFICIENT_BALANCE).build()).build();
        } catch(SameAccountTransferException sat) {
            logger.error(sat.getMessage(), sat);
            return Response.status(400).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_SAME_ACCOUNT_TRANSFER).build()).build();
        } catch(DataValidationException dve) {
            logger.error(dve.getMessage(), dve);
            return Response.status(400).entity(MessageVO.builder().message(ApplicationConstants.RESPONSE_ERROR_DATA_VALIDATION_FAILED_WITH+dve.getFieldNames()).build()).build();
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransaction(@PathParam("user_id") Integer userId, @PathParam("account_id") Integer accountId,@PathParam("id") Integer id) {
        try {
            Transaction result = transactionDao.getTransaction(id);
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
