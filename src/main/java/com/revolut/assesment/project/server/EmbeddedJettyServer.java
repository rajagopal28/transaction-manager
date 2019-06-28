package com.revolut.assesment.project.server;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

public class EmbeddedJettyServer {
    static final int DEFAULT_API_ENDPOINT_PORT = 8086;
    static final String DEFAULT_API_ENDPOINT_HOST = "localhost";
    static final String APPLICATION_RESOURCE_PACKAGE = "com.revolut.assesment.project.controller";

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedJettyServer.class);

    public static void main(String[] args) throws Exception {
        logger.info("Starting Embedded Jersey HTTPServer...\n");
        HttpServer httpServer = createHttpServer();
        httpServer.start();

        logger.info(String.format("\nJersey Application Server started with WADL available at " + "%sapplication.wadl\n", getURI()));
        logger.info("Started Embedded Jersey HTTPServer Successfully !!!");
    }

    private static HttpServer createHttpServer() throws IOException {
        ResourceConfig resourceConfig = new PackagesResourceConfig(APPLICATION_RESOURCE_PACKAGE);
        //resourceConfig.getContainerResponseFilters().add(AllServerCORSFilter.class);
        return HttpServerFactory.create(getURI(), resourceConfig);
    }

    private static URI getURI() {
        return UriBuilder.fromUri("http://" + getHostName() + "/").port(DEFAULT_API_ENDPOINT_PORT).build();
    }

    static String getHostName() {
        String hostName = DEFAULT_API_ENDPOINT_HOST;
        try {
            hostName = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        }
        return hostName;
    }
}
