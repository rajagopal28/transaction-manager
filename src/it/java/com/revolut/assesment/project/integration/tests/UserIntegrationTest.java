package com.revolut.assesment.project.integration.tests;

import com.revolut.assesment.project.constants.ApplicationConstants;
import com.revolut.assesment.project.controller.UserController;
import com.revolut.assesment.project.model.User;
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

public class UserIntegrationTest extends BaseIntegrationTest {
    private static HttpServer server;
    private static final String TEST_ENDPOINT_HOST = "http://localhost";
    private static final int TEST_ENDPOINT_PORT = 8282;
    private final String testPathUsers = "/users/";

    @BeforeClass
    public static void setUp() throws Exception{
        ResourceConfig resourceConfig = new ClassNamesResourceConfig(UserController.class);
        BaseIntegrationTest.createAndStartServerForConfig(TEST_ENDPOINT_HOST, TEST_ENDPOINT_PORT, resourceConfig);
    }

    @Test
    public void testEmptyResponse() {
        Response response = RestAssured.get(TEST_ENDPOINT_HOST+":"+TEST_ENDPOINT_PORT+testPathUsers);
        response.then().statusCode(200).body("A", Matchers.empty());
    }

    @Test
    public void testMultiUsersResponse() {
        final List<User> users = createNUsers(2);
        persistAllUsers(users);
        System.out.println(users);
        Response response = RestAssured.get(TEST_ENDPOINT_HOST+":"+TEST_ENDPOINT_PORT+testPathUsers);
        response.then().statusCode(200).body("A", Matchers.hasSize(2));

        response.then().body("id", CoreMatchers.hasItems(users.stream().map(User::getId).collect(Collectors.toSet()).toArray()));
        response.then().body("firstName", CoreMatchers.hasItems(users.stream().map(User::getFirstName).collect(Collectors.toSet()).toArray()));
        response.then().body("email", CoreMatchers.hasItems(users.stream().map(User::getEmail).collect(Collectors.toSet()).toArray()));
        response.then().body("phoneNumber", CoreMatchers.hasItems(users.stream().map(User::getPhoneNumber).collect(Collectors.toSet()).toArray()));

        deleteAllUsers(users);
    }

    @Test
    public void testSingleUserResponse() {
        final List<User> users = createNUsers(2);
        persistAllUsers(users);
        User user = users.get(1);
        Response response = RestAssured.get(TEST_ENDPOINT_HOST+":"+TEST_ENDPOINT_PORT+ testPathUsers +user.getId());
        response.then().statusCode(200);

        response.then().body("id", CoreMatchers.is(user.getId()));
        response.then().body("firstName", CoreMatchers.is(user.getFirstName()));
        response.then().body("email", CoreMatchers.is(user.getEmail()));
        response.then().body("phoneNumber", CoreMatchers.is(user.getPhoneNumber()));

        deleteAllUsers(users);
    }

    @Test
    public void testSingleUserResponseUnAvailable() {

        Response response = RestAssured.get(TEST_ENDPOINT_HOST + ":" + TEST_ENDPOINT_PORT + "/users/1");
        response.then().statusCode(404);
        response.then().body("message",  Matchers.is("Unable to Find Record with given data!"));
    }

    @Test
    public void testCreateUserResponse() {
        final List<User> users = createNUsers(1);
        User user = users.get(0);
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user)
                .post(TEST_ENDPOINT_HOST+":"+TEST_ENDPOINT_PORT+ testPathUsers);
        response.then().statusCode(201);

        Integer id = response.path("id");
        response.then().body("firstName", CoreMatchers.is(user.getFirstName()));
        response.then().body("email", CoreMatchers.is(user.getEmail()));
        response.then().body("phoneNumber", CoreMatchers.is(user.getPhoneNumber()));

        user.setId(id);
        deleteAllUsers(users);
    }

    @Test
    public void testCreateUserMissingFieldResponse() {
        final List<User> users = createNUsers(1);
        User user = users.get(0);
        user.setCity(null);
        user.setEmail("");
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(user)
                .post(TEST_ENDPOINT_HOST+":"+TEST_ENDPOINT_PORT+ testPathUsers);
        response.then().statusCode(400);

        response.then().body("message", CoreMatchers.is("Required Field(s) are Invalid! Field(s) :[city, email]"));
    }

    private List<User> createNUsers(int n) {
        List<User> users = new ArrayList<>();
        for (int i = 1; i <=n ; i++) {
            User u = User.builder().city("city"+i)
                    .firstName("firstName"+i)
                    .lastName("lastName"+i)
                    .email("firstLast"+i+"@email.com")
                    .gender(i%2 ==0 ? "Male" : "Female")
                    .dob("25/"+(i%12 +1)+"/1990")
                    .phoneNumber("phoneNumber"+i)
                    .build();
            users.add(u);
        }
        return users;
    }

    private void persistAllUsers(List<User> users) {
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory(ApplicationConstants.SQLITE_DB_NAME);
        final EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        users.forEach(em::persist);
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    private void deleteAllUsers(List<User> users) {
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory(ApplicationConstants.SQLITE_DB_NAME);
        final EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        users.stream().map(u-> em.find(User.class, u.getId())).forEach(em::remove);
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    @AfterClass
    public static void tearDown() {
        BaseIntegrationTest.stopServer();
    }
}
