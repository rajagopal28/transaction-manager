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

public class TransactionIntegrationTest {
    private static HttpServer server;
    private static final String TEST_ENDPOINT_HOST = "http://localhost";
    private static final int TEST_ENDPOINT_PORT = 8484;

    @BeforeClass
    public static void setUp() throws Exception{
        URI uri = UriBuilder.fromUri(TEST_ENDPOINT_HOST+"/").port(TEST_ENDPOINT_PORT).build();
        // Create an HTTP server listening at port TEST_ENDPOINT_PORT
        server = HttpServer.create(new InetSocketAddress(uri.getPort()), 0);
        // Create a handler wrapping the JAX-RS application
        ResourceConfig resourceConfig = new ClassNamesResourceConfig(TransactionController.class);
        HttpHandler handler = RuntimeDelegate.getInstance().createEndpoint(resourceConfig, HttpHandler.class);
        // Map JAX-RS handler to the server root
        server.createContext(uri.getPath(), handler);
        server.start();
    }

    @Test
    public void testEmptyResponse() throws Exception {
        Response response = RestAssured.get(TEST_ENDPOINT_HOST+":"+TEST_ENDPOINT_PORT+"/users/1/accounts/1/transactions");
        response.then().statusCode(200).body("A", Matchers.empty());
    }

    @Test
    public void testSingleTransactionResponseUnAvailable() throws Exception {
        Response response = RestAssured.get(TEST_ENDPOINT_HOST + ":" + TEST_ENDPOINT_PORT + "/users/1/accounts/1/transactions/1");
        response.then().statusCode(200);
        response.then().body(Matchers.isEmptyString());
    }

    @Test
    public void testMultiTransactionsInAccountResponse() throws Exception {
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
    public void testSingleAccountResponse() throws Exception {
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
    public void testPerformDepositTransaction() throws Exception {
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
    public void testPerformSameAccountTransfer() throws Exception {
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
        List<Transaction> transactions2 = createNTransactionsInAccountInUser(1);
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
        server.stop(0);
    }
}
