package com.revolut.assesment.project.dao.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DriverManager.class, SQLiteConnectionManager.class})
public class SQLiteConnectionManagerTest {

    @Test
    public void testGetConnectionWithAutoCommit() throws Exception {

        PowerMockito.mockStatic(DriverManager.class);

        Connection mockConnection = PowerMockito.mock(Connection.class);
        BDDMockito.given(DriverManager.getConnection(Mockito.anyString())).willReturn(mockConnection);

        SQLiteConnectionManager connectionManager = PowerMockito.spy(new SQLiteConnectionManager());
        Connection actualConnection = connectionManager.getConnection();


        assertEquals(mockConnection, actualConnection);
        Mockito.verify(mockConnection).setAutoCommit(true);
    }


    @Test
    public void testGetConnectionWithoutAutoCommit() throws Exception {

        PowerMockito.mockStatic(DriverManager.class);

        Connection mockConnection = PowerMockito.mock(Connection.class);
        BDDMockito.given(DriverManager.getConnection(Mockito.anyString())).willReturn(mockConnection);

        SQLiteConnectionManager connectionManager = PowerMockito.spy(new SQLiteConnectionManager());
        Connection actualConnection = connectionManager.getConnection(false);


        assertEquals(mockConnection, actualConnection);
        Mockito.verify(mockConnection).setAutoCommit(false);
    }

}