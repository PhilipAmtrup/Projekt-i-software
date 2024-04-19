package dk.dtu.compute.se.pisd.roborally.controller;
import dk.dtu.compute.se.pisd.roborally.dal.Repository;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;

import dk.dtu.compute.se.pisd.roborally.dal.Connector;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;

public class JDBC_test {

    @Test
    public void testConnection() {
        Connector connector = new Connector();
        assertNotNull(connector.getConnection(), "Connection should not be null");

        try {
            assertTrue(!connector.getConnection().isClosed(), "Connection should be open");
        } catch (SQLException e) {
            fail("SQLException should not be thrown when checking if connection is open");
        }
    }

}

