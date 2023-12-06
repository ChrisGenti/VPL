package com.github.chrisgenti.vpl.velocity.data.mysql.connectors.hikari;

import com.github.chrisgenti.vpl.velocity.data.mysql.connectors.Connector;
import com.github.chrisgenti.vpl.velocity.data.mysql.settings.PoolSettings;
import com.github.chrisgenti.vpl.velocity.data.mysql.utils.DataSourceUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class HikariConnector implements Connector {
    private HikariConfig config;
    private MariaDbDataSource mariaSource;
    private HikariDataSource source = null;

    public HikariConnector(HikariDataSource source) {
        this.source = source;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public HikariConnector(String hostname, int port, String database, String username, String password, PoolSettings settings) {
        this.config = new HikariConfig();

        this.mariaSource = new MariaDbDataSource();
        try {
            this.mariaSource.setUrl("jdbc:mariadb://" + hostname + ":" + port + "/" + database);
            this.mariaSource.setUser(username);
            this.mariaSource.setPassword(password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        config.setDataSource(this.mariaSource);

        config.setPoolName(settings.getPoolName());
        config.setMaximumPoolSize(settings.getMaximumPoolSize());
        config.setMinimumIdle(settings.getMinimumIdle());
        config.setInitializationFailTimeout(settings.getInitializationFailTimeout());
        config.setConnectionTimeout(settings.getConnectionTimeout());
        config.setIdleTimeout(settings.getIdleTimeout());
        config.setMaxLifetime(settings.getMaxLifetime());
        config.setLeakDetectionThreshold(settings.getLeakDetectionThreshold());
        config.setConnectionTestQuery("SELECT 1");

        for (Map.Entry<String, String> property : settings.getDataSourceProperties().entrySet()) {
            config.addDataSourceProperty(property.getKey(), property.getValue());
        }

        for (Map.Entry<String, String> property : settings.getDataSourceProperties().entrySet()) {
            config.addHealthCheckProperty(property.getKey(), property.getValue());
        }
    }

    @Override
    public boolean check() {
        Connection connection = null;
        try {
            connection = mariaSource.getConnection();
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            DataSourceUtils.closeConnection(connection);
        }
    }

    @Override
    public void setup() {
        this.source = new HikariDataSource(this.config);
    }

    @Override
    public Connection connection() throws SQLException {
        if (source == null)
            throw new SQLException("Unable to get a connection from the pool. (dataSource is null)");
        Connection connection = source.getConnection();

        if (connection == null)
            throw new SQLException("Unable to get a connection from the pool. (connection is null)");
        return connection;
    }

    @Override
    public void shutdown() {
        if (source != null)
            source.close();
    }
}
