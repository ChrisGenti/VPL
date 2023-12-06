package com.github.chrisgenti.vpl.velocity.data.mysql.connectors;

import java.sql.Connection;
import java.sql.SQLException;

public interface Connector {
    boolean check();

    void setup();

    Connection connection() throws SQLException;

    void shutdown();
}
