package com.revolut.assesment.project.server;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.net.httpserver.HttpServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.UriBuilder;
import java.net.InetAddress;
import java.net.URI;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EmbeddedJettyServer.class, HttpServerFactory.class, UriBuilder.class, InetAddress.class})
public class EmbeddedJettyServerTest {

    @Test
    public void testStartServerScenarioWithGivenIP() throws Exception {

        HttpServer mockServer = Mockito.mock(HttpServer.class);
        InetAddress inetAddress = Mockito.mock(InetAddress.class);

        UriBuilder mockURIBuilder = Mockito.mock(UriBuilder.class);

        String hostName = "hostName";
        URI mockURI = URI.create(hostName);

        Mockito.when(mockURIBuilder.port(Mockito.anyInt())).thenReturn(mockURIBuilder);
        Mockito.when(mockURIBuilder.build()).thenReturn(mockURI);

        Mockito.when(inetAddress.getCanonicalHostName()).thenReturn(hostName);

        PowerMockito.mockStatic(UriBuilder.class);
        PowerMockito.doReturn(mockURIBuilder).when(UriBuilder.class, "fromUri", Mockito.anyString());

        PowerMockito.spy(EmbeddedJettyServer.class);
        PowerMockito.doReturn(hostName).when(EmbeddedJettyServer.class, "getHostName");


        PowerMockito.mockStatic(HttpServerFactory.class);
        PowerMockito.doReturn(mockServer).when(HttpServerFactory.class, "create" , Mockito.any(URI.class), Mockito.any(ResourceConfig.class));

        EmbeddedJettyServer.main(new String[] {});

        Mockito.verify(mockURIBuilder, Mockito.times(2)).build();
        Mockito.verify(mockServer).start();
    }

}