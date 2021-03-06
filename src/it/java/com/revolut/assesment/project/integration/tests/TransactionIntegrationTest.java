package com.revolut.assesment.project.integration.tests;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.controller.TransactionController;
import com.revolut.assesment.project.model.Account;
import com.revolut.assesment.project.model.Transaction;
import com.revolut.assesment.project.model.User;
import com.revolut.assesment.project.vo.TransactionVO;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionIntegrationTest extends BaseIntegrationTest {
    private static HttpServer server;
    private static final String TEST_ENDPOINT_HOST = "http://localhost";
    private static final int TEST_ENDPOINT_PORT = 8484;

    @BeforeClass
    public static void setUp() throws Exception{
        ResourceConfig resourceConfig = new ClassNamesResourceConfig(TransactionController.class);
        BaseIntegrationTest.createAndStartServerForConfig(TEST_ENDPOINT_HOST, TEST_ENDPOINT_PORT, resourceConfig);
    }

    @Test
    public void testEmptyResponse() {
        Response response = RestAssured.get(TEST_ENDPOINT_HOST+":"+TEST_ENDPOINT_PORT+"/users/1/accounts/1/transactions");
        response.then().statusCode(200).body("A", Matchers.empty());
    }

    @Test
    public void testSingleTransactionResponseUnAvailable() {
        Response response = RestAssured.get(TEST_ENDPOINT_HOST + ":" + TEST_ENDPOINT_PORT + "/users/1/accounts/1/transactions/1");
        response.then().statusCode(404);
        response.then().body("message",  Matchers.is("Unable to Find Record with given data!"));
    }

    @Test
    public void testMultiTransactionsInAccountResponse() {
        User user = User.builder().city("city1")
                .firstName("firstName1")
                .lastName("lastName1")
                .email("email1")
                .phoneNumber("phoneNumber1")
                .dob("23/12/1990")
                .gender("Female")
                .build();
        Account account = Account.builder()
                .accountNumber("accountNumber2")
                .accountType(ApplicationConstants.AccountType.SAVINGS)
                .balance(1012.00)
                .currency("GBP")
                .timeCreated(System.currentTimeMillis())
                .build();

        List<Transaction> transactions = createNTransactionsInAccountInUser(2);
        persistAllTransactionsInAccountInUser(transactions, account, user);
        System.out.println(transactions);
        Response response = RestAssured.get(TEST_ENDPOINT_HOST+":"+TEST_ENDPOINT_PORT+"/users/"+user.getId()+"/accounts/"+account.getId()+"/transactions/");
        response.then().statusCode(200).body("A", Matchers.hasSize(2));

        response.then().body("id", CoreMatchers.hasItems(transactions.stream().map(Transaction::getId).collect(Collectors.toList()).toArray()));
        response.then().body("transactionType", CoreMatchers.hasItems(transactions.stream().map(Transaction::getTransactionType).map(s -> s.toString()).collect(Collectors.toList()).toArray()));
        response.then().body("currency", CoreMatchers.hasItems(transactions.stream().map(Transaction::getCurrency).collect(Collectors.toList()).toArray()));
        response.then().body("timeCreated", CoreMatchers.hasItems(transactions.stream().map(Transaction::getTimeCreated).collect(Collectors.toList()).toArray()));

        deleteAllTransactionsInAccountAndUser(transactions, account,  user);
    }


    @Test
    public void testSingleAccountResponse() {
        User user = User.builder().city("city1")
                .firstName("firstName1")
                .lastName("lastName1")
                .email("email1")
                .phoneNumber("phoneNumber1")
                .dob("23/12/1990")
                .gender("Female")
                .build();
        Account account = Account.builder()
                .accountNumber("accountNumber2")
                .accountType(ApplicationConstants.AccountType.SAVINGS)
                .balance(1012.00)
                .currency("GBP")
                .timeCreated(System.currentTimeMillis())
                .build();

        List<Transaction> transactions = createNTransactionsInAccountInUser(2);
        persistAllTransactionsInAccountInUser(transactions, account, user);
        System.out.println(transactions);
        Transaction transaction = transactions.get(1);
        Response response = RestAssured.get(TEST_ENDPOINT_HOST+":"+TEST_ENDPOINT_PORT+"/users/"+user.getId()+"/accounts/"+account.getId()+"/transactions/"+transaction.getId());
        response.then().statusCode(200);

        response.then().body("id", CoreMatchers.is(transaction.getId()));
        response.then().body("transactionType", CoreMatchers.is(transaction.getTransactionType().toString()));
        response.then().body("currency", CoreMatchers.is(transaction.getCurrency()));
        response.then().body("timeCreated", CoreMatchers.is(transaction.getTimeCreated()));

        deleteAllTransactionsInAccountAndUser(transactions, account, user);
    }

    @Test
    public void testPerformDepositTransaction() {
        User user = User.builder().city("city1")
                .firstName("firstName1")
                .lastName("lastName1")
                .email("email1")
                .phoneNumber("phoneNumber1")
                .dob("23/12/1990")
                .gender("Female")
                .build();
        Account account = Account.builder()
                .accountNumber("accountNumber2")
                .accountType(ApplicationConstants.AccountType.SAVINGS)
                .balance(1012.00)
                .currency("GBP")
                .timeCreated(System.currentTimeMillis())
                .build();

        List<Transaction> transactions = createNTransactionsInAccountInUser(1);
        persistAllTransactionsInAccountInUser(new ArrayList<>(), account, user);
        System.out.println(transactions);
        Transaction transaction = transactions.get(0);
        TransactionVO transactionVO = new TransactionVO(null, account.getId(), transaction.getAmount(), transaction.getCurrency(), ApplicationConstants.TransactionType.CASH_DEPOSIT);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(transactionVO)
                .post(TEST_ENDPOINT_HOST+":"+TEST_ENDPOINT_PORT+"/users/"+user.getId()+"/accounts/"+account.getId()+"/transactions/");
        response.then().statusCode(201);

        Integer id = response.path("id");
        response.then().body("transactionType", CoreMatchers.is(transaction.getTransactionType().toString()));
        response.then().body("currency", CoreMatchers.is(transaction.getCurrency()));
        response.then().body("toAccount.id", CoreMatchers.is(account.getId()));

        transaction.setId(id);
        deleteAllTransactionsInAccountAndUser(transactions, account, user);
    }

    @Test
    public void testPerformTwoAccountTransfer() {
        User user = User.builder().city("city1")
                .firstName("firstName1")
                .lastName("lastName1")
                .email("email1")
                .phoneNumber("phoneNumber1")
                .dob("23/12/1990")
                .gender("Female")
                .build();
        Account account = Account.builder()
                .accountNumber("accountNumber2")
                .accountType(ApplicationConstants.AccountType.SAVINGS)
                .balance(1012.00)
                .currency("GBP")
                .timeCreated(System.currentTimeMillis())
                .build();


        User user2 = User.builder().city("city2")
                .firstName("firstName2")
                .lastName("lastName2")
                .email("email2")
                .phoneNumber("phoneNumber2")
                .dob("2/12/1990")
                .gender("Male")
                .build();
        Account account2 = Account.builder()
                .accountNumber("accountNumber3")
                .accountType(ApplicationConstants.AccountType.SAVINGS)
                .balance(1012.00)
                .currency("GBP")
                .timeCreated(System.currentTimeMillis())
                .build();


        List<Transaction> transactions = createNTransactionsInAccountInUser(1);
        persistAllTransactionsInAccountInUser(new ArrayList<>(), account, user);
        persistAllTransactionsInAccountInUser(new ArrayList<>(), account2, user2);
        System.out.println(transactions);
        Transaction transaction = transactions.get(0);
        TransactionVO transactionVO = new TransactionVO(account2.getId(), account.getId(), transaction.getAmount(), transaction.getCurrency(), ApplicationConstants.TransactionType.CASH_DEPOSIT);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(transactionVO)
                .post(TEST_ENDPOINT_HOST+":"+TEST_ENDPOINT_PORT+"/users/"+user.getId()+"/accounts/"+account.getId()+"/transactions/");
        response.then().statusCode(201);

        Integer id = response.path("id");
        response.then().body("transactionType", CoreMatchers.is(transaction.getTransactionType().toString()));
        response.then().body("currency", CoreMatchers.is(transaction.getCurrency()));
        response.then().body("toAccount.id", CoreMatchers.is(account.getId()));

        transaction.setId(id);
        deleteAllTransactionsInAccountAndUser(transactions, account, user);
        deleteAllTransactionsInAccountAndUser(new ArrayList<>(), account2, user2);
    }

    @Test
    public void testPerformDepositFailureWithInvalidData() {

        TransactionVO transactionVO = new TransactionVO(null, null, 0.00, null, ApplicationConstants.TransactionType.CASH_DEPOSIT);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(transactionVO)
                .post(TEST_ENDPOINT_HOST+":"+TEST_ENDPOINT_PORT+"/users/1/accounts/1/transactions/");
        response.then().statusCode(400);

        response.then().body("message", CoreMatchers.is("Required Field(s) are Invalid! Field(s) :[currency, amount]"));

    }

    @Test
    public void testPerformTwoAccountTransferFailureWithInvalidData() {

        TransactionVO transactionVO = new TransactionVO(null, null, 0.00, null, ApplicationConstants.TransactionType.TRANSFER);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(transactionVO)
                .post(TEST_ENDPOINT_HOST+":"+TEST_ENDPOINT_PORT+"/users/1/accounts/1/transactions/");
        response.then().statusCode(400);

        response.then().body("message", CoreMatchers.is("Required Field(s) are Invalid! Field(s) :[fromAccountId, currency, amount]"));

    }

    @Test
    public void testPerformTwoAccountTransferCurrencyMismatchException() {
        User user = User.builder().city("city1")
                .firstName("firstName1")
                .lastName("lastName1")
                .email("email1")
                .phoneNumber("phoneNumber1")
                .dob("23/12/1990")
                .gender("Female")
                .build();
        Account account = Account.builder()
                .accountNumber("accountNumber2")
                .accountType(ApplicationConstants.AccountType.SAVINGS)
                .balance(1012.00)
                .currency("GBP")
                .timeCreated(System.currentTimeMillis())
                .build();


        User user2 = User.builder().city("city2")
                .firstName("firstName2")
                .lastName("lastName2")
                .email("email2")
                .phoneNumber("phoneNumber2")
                .dob("2/12/1990")
                .gender("Male")
                .build();
        Account account2 = Account.builder()
                .accountNumber("accountNumber3")
                .accountType(ApplicationConstants.AccountType.SAVINGS)
                .balance(1012.00)
                .currency("GBP")
                .timeCreated(System.currentTimeMillis())
                .build();


        List<Transaction> transactions = createNTransactionsInAccountInUser(1);
        persistAllTransactionsInAccountInUser(new ArrayList<>(), account, user);
        persistAllTransactionsInAccountInUser(new ArrayList<>(), account2, user2);
        System.out.println(transactions);
        Transaction transaction = transactions.get(0);
        TransactionVO transactionVO = new TransactionVO(account2.getId(), account.getId(), transaction.getAmount(), transaction.getCurrency(), ApplicationConstants.TransactionType.CASH_DEPOSIT);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(transactionVO)
                .post(TEST_ENDPOINT_HOST+":"+TEST_ENDPOINT_PORT+"/users/"+user.getId()+"/accounts/"+account.getId()+"/transactions/");
        response.then().statusCode(201);

        Integer id = response.path("id");
        response.then().body("transactionType", CoreMatchers.is(transaction.getTransactionType().toString()));
        response.then().body("currency", CoreMatchers.is(transaction.getCurrency()));
        response.then().body("toAccount.id", CoreMatchers.is(account.getId()));

        transaction.setId(id);
        deleteAllTransactionsInAccountAndUser(transactions, account, user);
        deleteAllTransactionsInAccountAndUser(new ArrayList<>(), account2, user2);
    }

    @Test
    public void testPerformSameAccountTransferException() {
        User user = User.builder().city("city1")
                .firstName("firstName1")
                .lastName("lastName1")
                .email("email1")
                .phoneNumber("phoneNumber1")
                .dob("23/12/1990")
                .gender("Female")
                .build();
        Account account = Account.builder()
                .accountNumber("accountNumber2")
                .accountType(ApplicationConstants.AccountType.SAVINGS)
                .balance(1012.00)
                .currency("GBP")
                .timeCreated(System.currentTimeMillis())
                .build();

        List<Transaction> transactions = createNTransactionsInAccountInUser(1);
        persistAllTransactionsInAccountInUser(new ArrayList<>(), account, user);
        System.out.println(transactions);
        Transaction transaction = transactions.get(0);
        TransactionVO transactionVO = new TransactionVO(account.getId(), account.getId(), transaction.getAmount(), transaction.getCurrency(), ApplicationConstants.TransactionType.TRANSFER);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(transactionVO)
                .post(TEST_ENDPOINT_HOST+":"+TEST_ENDPOINT_PORT+"/users/"+user.getId()+"/accounts/"+account.getId()+"/transactions/");
        response.then().statusCode(400);
        response.then().body("message", CoreMatchers.is("Cannot transfer within the Same Account!"));

        deleteAllTransactionsInAccountAndUser(new ArrayList<>(), account, user);
    }


    private List<Transaction> createNTransactionsInAccountInUser(int n) {
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 1; i <=n ; i++) {
            Transaction a = Transaction.builder()
                    .transactionType(ApplicationConstants.TransactionType.CASH_DEPOSIT)
                    .amount(i*Math.random()*999)
                    .currency("GBP")
                    .timeCreated(System.currentTimeMillis())
                    .build();
            transactions.add(a);
        }
        return transactions;
    }

    private void persistAllTransactionsInAccountInUser(List<Transaction> transactions, Account account, User user) {
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory(ApplicationConstants.SQLITE_DB_NAME);
        final EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(user);
        account.setUser(user);
        em.persist(account);
        transactions.forEach(t -> t.setToAccount(account));
        transactions.forEach(em::persist);
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    private void deleteAllTransactionsInAccountAndUser(List<Transaction> transactions, Account account, User user) {
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory(ApplicationConstants.SQLITE_DB_NAME);
        final EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        transactions.stream().map(u-> em.find(Transaction.class, u.getId())).forEach(em::remove);
        em.remove(em.find(Account.class, account.getId()));
        em.remove(em.find(User.class, user.getId()));
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    @AfterClass
    public static void tearDown() {
        BaseIntegrationTest.stopServer();
    }
}
