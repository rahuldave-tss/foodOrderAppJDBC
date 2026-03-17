package com.tss.config;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class DBConnectionTest {
    @Test
    void shouldConnectToDb(){
        Connection connection=DBConnection.connect();

        assertNotNull(connection,"Connection should not be null");
    }

}