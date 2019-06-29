package com.revolut.assesment.project.integration.tests;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

class BaseIntegrationTest {
    static HttpServer server;

    static void createAndStartServerForConfig(String serverHost, int serverPort, ResourceConfig resourceConfig) throws IOException {
        URI uri = UriBuilder.fromUri(serverHost+"/").port(serverPort).build();
        // Create an HTTP server listening at port TEST_ENDPOINT_PORT
        server = HttpServer.create(new InetSocketAddress(uri.getPort()), 0);
        // Create a handler wrapping the JAX-RS application
        HttpHandler handler = RuntimeDelegate.getInstance().createEndpoint(resourceConfig, HttpHandler.class);
        // Map JAX-RS handler to the server root
        server.createContext(uri.getPath(), handler);
        server.start();
    }

    static void stopServer() {
        server.stop(0);
    }

}
