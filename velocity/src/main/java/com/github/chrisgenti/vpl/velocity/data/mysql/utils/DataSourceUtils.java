package com.github.chrisgenti.vpl.velocity.data.mysql.utils;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DataSourceUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceUtils.class);

    public static void close(@Nullable Connection connection, @Nullable PreparedStatement statement, @Nullable ResultSet result) {
        closeConnection(connection); closeStatement(statement); closeResult(result);
    }

    public static void closeConnection(@Nullable Connection connection) {
        if (connection == null)
            return;

        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.warn("There was an error while trying to close the connection.", e);
        }
    }

    public static void closeStatement(@Nullable Statement statement) {
        if (statement == null)
            return;

        try {
            statement.close();
        } catch (Throwable e) {
            LOGGER.warn("There was an error while trying to close the statement", e);
        }
    }

    public static void closeResult(@Nullable ResultSet result) {
        if (result == null)
            return;

        try {
            result.close();
        } catch (Throwable e) {
            LOGGER.warn("There was an error while trying to close the connection.", e);
        }
    }
}
