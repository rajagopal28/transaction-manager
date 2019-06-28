package com.revolut.assesment.project.integration.tests;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.controller.AccountController;
import com.revolut.assesment.project.model.Account;
import com.revolut.assesment.project.model.User;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.restassured.RestAssured;
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
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountIntegrationTest {
    private static HttpServer server;
    private static final String TEST_ENDPOINT_HOST = "http://localhost";
    private static final int TEST_ENDPOINT_PORT = 8383;

    @BeforeClass
    public static void setUp() throws Exception{
        URI uri = UriBuilder.fromUri(TEST_ENDPOINT_HOST+"/").port(TEST_ENDPOINT_PORT).build();
        // Create an HTTP server listening at port 8282
        server = HttpServer.create(new InetSocketAddress(uri.getPort()), 0);
        // Create a handler wrapping the JAX-RS application
        ResourceConfig resourceConfig = new ClassNamesResourceConfig(AccountController.class);
        HttpHandler handler = RuntimeDelegate.getInstance().createEndpoint(resourceConfig, HttpHandler.class);
        // Map JAX-RS handler to the server root
        server.createContext(uri.getPath(), handler);
        server.start();
    }

    @Test
    public void testEmptyResponse() throws Exception {
        Response response = RestAssured.get(TEST_ENDPOINT_HOST+":"+TEST_ENDPOINT_PORT+"/users/1/accounts/");
        response.then().statusCode(200).body("A", Matchers.empty());
    }

    @Test
    public void testMultiUsersResponse() throws Exception {
        User user = User.builder().city("city1")
                .firstName("firstName1")
                .lastName("lastName1")
                .email("email1")
                .phoneNumber("phoneNumber1")
                .dob("23/12/1990")
                .gender("Female")
                .build();
        final List<Account> accounts = createNAccountsInUser(2);
        persistAllAccountsInUser(accounts, user);
        System.out.println(accounts);
        Response response = RestAssured.get(TEST_ENDPOINT_HOST+":"+TEST_ENDPOINT_PORT+"/users/"+user.getId()+"/accounts/");
        response.then().statusCode(200).body("A", Matchers.hasSize(2));

        response.then().body("id", CoreMatchers.hasItems(accounts.stream().map(Account::getId).collect(Collectors.toList()).toArray()));
        response.then().body("accountType", CoreMatchers.hasItems(accounts.stream().map(Account::getAccountType).map(s -> s.toString()).collect(Collectors.toList()).toArray()));
        response.then().body("accountNumber", CoreMatchers.hasItems(accounts.stream().map(Account::getAccountNumber).collect(Collectors.toList()).toArray()));
//        response.then().body("balance", BigDecimalCloseTo.closeTo(accounts.stream().map(Account::getBalance).map(BigDecimal::new).collect(Collectors.toList())));

        deleteAllAccountsAndUser(accounts, user);
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
        final List<Account> accounts = createNAccountsInUser(2);
        persistAllAccountsInUser(accounts, user);
        System.out.println(accounts);
        Account account = accounts.get(1);
        Response response = RestAssured.get(TEST_ENDPOINT_HOST+":"+TEST_ENDPOINT_PORT+"/users/"+user.getId()+"/accounts/"+account.getId());
        response.then().statusCode(200);

        response.then().body("id", CoreMatchers.is(account.getId()));
        response.then().body("accountNumber", CoreMatchers.is(account.getAccountNumber()));
        response.then().body("accountType", CoreMatchers.is(account.getAccountType().toString()));
//        response.then().body("balance", BigDecimalCloseTo.closeTo(new BigDecimal(account.getBalance()), new BigDecimal(0.001)));

        deleteAllAccountsAndUser(accounts, user);
    }

    private List<Account> createNAccountsInUser(int n) {
        List<Account> accounts = new ArrayList<>();
        for (int i = 1; i <=n ; i++) {
            Account a = Account.builder()
                    .accountNumber("accountNumber"+i)
                    .accountType(ApplicationConstants.AccountType.SAVINGS)
                    .balance(i*Math.random()*100)
                    .currency("GBP")
                    .timeCreated(new Date(System.currentTimeMillis()))
                    .build();
            accounts.add(a);
        }
        return accounts;
    }

    private void persistAllAccountsInUser(List<Account> accounts, User user) {
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory(ApplicationConstants.SQLITE_DB_NAME);
        final EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(user);
        accounts.forEach(a -> a.setUser(user));
        accounts.forEach(em::persist);
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    private void deleteAllAccountsAndUser(List<Account> accounts, User user) {
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory(ApplicationConstants.SQLITE_DB_NAME);
        final EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        accounts.stream().map(u-> em.find(Account.class, u.getId())).forEach(em::remove);
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
