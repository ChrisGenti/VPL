package com.github.chrisgenti.vpl.velocity.data.mysql;

import com.github.chrisgenti.vpl.velocity.VPLPlugin;
import com.github.chrisgenti.vpl.velocity.data.DataProvider;
import com.github.chrisgenti.vpl.velocity.data.mysql.connectors.Connector;
import com.github.chrisgenti.vpl.velocity.data.mysql.connectors.hikari.HikariConnector;
import com.github.chrisgenti.vpl.velocity.data.mysql.credentials.Credentials;
import com.github.chrisgenti.vpl.velocity.data.mysql.settings.PoolSettings;
import com.github.chrisgenti.vpl.velocity.data.mysql.utils.DataSourceUtils;
import com.google.common.collect.Lists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection", "CallToPrintStackTrace"})
public class MySQLProvider implements DataProvider {
    private static final String USERS_TABLE = "vpl_users";
    private static final String IPS_TABLE = "vpl_ips";
    private Connector connector;

    public MySQLProvider(VPLPlugin plugin, Credentials credentials) {
        this.connector = new HikariConnector(credentials.hostname(), credentials.port(), credentials.database(), credentials.username(), credentials.password(), new PoolSettings());

        // * CHECK CONNECTION
        if (!this.connector.check()) {
            this.connector = null;

            // * SETUP MESSAGE
            plugin.sendMessage(
                    "<reset>", "<bold><dark_red>DATABASE</bold>", "<white>The database credentials are incorrect", "<white>a connection could not be created.", "<reset>"
            );
        } else {
            // * SETUP MESSAGE
            plugin.sendMessage(
                    "<reset>", "<bold><dark_green>DATABASE</bold>", "<white>The database credentials are correct", "<white>it was possible to create a connection.", "<reset>"
            );
        }
    }

    @Override
    public void init() {
        if (connector == null)
            return;

        connector.setup(); Connection connection = null; PreparedStatement statement = null;
        try {
            connection = connector.connection();

            statement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + USERS_TABLE + " (username VARCHAR(15) NOT NULL, uniqueID VARCHAR(36) NOT NULL, premium BOOLEAN NOT NULL, PRIMARY KEY (uniqueID));"
            ); statement.executeUpdate();

            statement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + IPS_TABLE + " (id INT NOT NULL AUTO_INCREMENT, uniqueID VARCHAR(36) NOT NULL, address VARCHAR(15) NOT NULL, PRIMARY KEY(id), FOREIGN KEY(uniqueID) REFERENCES " + USERS_TABLE + "(uniqueID) ON DELETE CASCADE);"
            ); statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            DataSourceUtils.closeConnection(connection); DataSourceUtils.closeStatement(statement);
        }
    }

    @Override
    public CompletableFuture<Boolean> presentInIP(UUID uniqueID) {
        return CompletableFuture.supplyAsync(() -> {
            if (connector == null)
                return false;

            Connection connection = null; PreparedStatement statement = null; ResultSet result = null;
            try {
                connection = connector.connection(); statement = connection.prepareStatement("SELECT * FROM " + IPS_TABLE + " WHERE uniqueID = ?;");
                statement.setString(1, uniqueID.toString()); result = statement.executeQuery();

                return result.next();
            } catch (SQLException exception) {
                exception.printStackTrace(); return false;
            } finally {
                DataSourceUtils.close(connection, statement, result);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> presentInUsers(UUID uniqueID) {
        return CompletableFuture.supplyAsync(() -> {
            if (connector == null)
                return false;

            Connection connection = null; PreparedStatement statement = null; ResultSet result = null;
            try {
                connection = connector.connection(); statement = connection.prepareStatement("SELECT * FROM " + USERS_TABLE + " WHERE uniqueID = ?;");
                statement.setString(1, uniqueID.toString()); result = statement.executeQuery();

                return result.next();
            } catch (SQLException exception) {
                exception.printStackTrace(); return false;
            } finally {
                DataSourceUtils.close(connection, statement, result);
            }
        });
    }

    @Override
    public CompletableFuture<String> getUserIP(UUID uniqueID) {
        return CompletableFuture.supplyAsync(() -> {
            if (connector == null)
                return "";

            Connection connection = null; PreparedStatement statement = null; ResultSet result = null;
            try {
                connection = connector.connection(); statement = connection.prepareStatement("SELECT * FROM " + IPS_TABLE + " WHERE uniqueID = ?;");
                statement.setString(1, uniqueID.toString()); result = statement.executeQuery();

                if (result.next())
                    return result.getString("address");
                return "";
            } catch (SQLException exception) {
                exception.printStackTrace(); return "";
            } finally {
                DataSourceUtils.close(connection, statement, result);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> getUserPremium(UUID uniqueID) {
        return CompletableFuture.supplyAsync(() -> {
            if (connector == null)
                return false;

            Connection connection = null; PreparedStatement statement = null; ResultSet result = null;
            try {
                connection = connector.connection(); statement = connection.prepareStatement("SELECT * FROM " + USERS_TABLE + " WHERE uniqueID = ?;");
                statement.setString(1, uniqueID.toString()); result = statement.executeQuery();

                if (result.next())
                    return result.getBoolean("premium");
                return false;
            } catch (SQLException exception) {
                exception.printStackTrace(); return false;
            } finally {
                DataSourceUtils.close(connection, statement, result);
            }
        });
    }

    @Override
    public CompletableFuture<List<String>> getUsers() {
        return CompletableFuture.supplyAsync(() -> {
            List<String> values = Lists.newArrayList();
            if (connector == null)
                return values;

            Connection connection = null; PreparedStatement statement = null; ResultSet result = null;
            try {
                connection = connector.connection(); statement = connection.prepareStatement("SELECT * FROM " + USERS_TABLE + " WHERE premium = ?;");
                statement.setBoolean(1, true); result = statement.executeQuery();

                while (result.next())
                    values.add(result.getString("username"));
                return values;
            } catch (SQLException exception) {
                exception.printStackTrace(); return values;
            } finally {
                DataSourceUtils.close(connection, statement, result);
            }
        });
    }

    @Override
    public CompletableFuture<List<String>> getUsersByIP(String address) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> values = Lists.newArrayList();
            if (connector == null)
                return values;

            Connection connection = null; PreparedStatement statement = null; ResultSet result = null;
            try {
                connection = connector.connection(); statement = connection.prepareStatement("SELECT " + USERS_TABLE + ".username AS username FROM " + USERS_TABLE + " INNER JOIN " + IPS_TABLE + " ON " + USERS_TABLE + ".uniqueID = " + IPS_TABLE + ".uniqueID AND address = ?;");
                statement.setString(1, address); result = statement.executeQuery();

                while (result.next())
                    values.add(result.getString("username"));
                return values;
            } catch (SQLException exception) {
                exception.printStackTrace(); return values;
            } finally {
                DataSourceUtils.close(connection, statement, result);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> addUser(UUID uniqueID, String address) {
        return CompletableFuture.supplyAsync(() -> {
            if (connector == null)
                return false;

            Connection connection = null; PreparedStatement statement = null;
            try {
                connection = connector.connection(); statement = connection.prepareStatement("INSERT INTO " + IPS_TABLE + " (uniqueID, address) VALUES(?, ?);");
                statement.setString(1, uniqueID.toString()); statement.setString(2, address); return statement.executeUpdate() != 0;
            } catch (SQLException exception) {
                exception.printStackTrace(); return false;
            } finally {
                DataSourceUtils.close(connection, statement, null);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> addUser(String username, UUID uniqueID, boolean premium) {
        return CompletableFuture.supplyAsync(() -> {
            if (connector == null)
                return false;

            Connection connection = null; PreparedStatement statement = null;
            try {
                connection = connector.connection(); statement = connection.prepareStatement("INSERT INTO " + USERS_TABLE + " VALUES(?, ?, ?);");
                statement.setString(1, username); statement.setString(2, uniqueID.toString()); statement.setBoolean(3, premium); return statement.executeUpdate() != 0;
            } catch (SQLException exception) {
                exception.printStackTrace(); return false;
            } finally {
                DataSourceUtils.close(connection, statement, null);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> editUser(UUID uniqueID, String address) {
        return CompletableFuture.supplyAsync(() -> {
            if (connector == null)
                return false;

            Connection connection = null; PreparedStatement statement = null;
            try {
                connection = connector.connection(); statement = connection.prepareStatement("UPDATE " + IPS_TABLE + " SET address = ? WHERE uniqueID = ?;");
                statement.setString(1, address); statement.setString(2, uniqueID.toString()); return statement.executeUpdate() != 0;
            } catch (SQLException exception) {
                exception.printStackTrace(); return false;
            } finally {
                DataSourceUtils.close(connection, statement, null);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> editUser(UUID uniqueID, boolean premium) {
        return CompletableFuture.supplyAsync(() -> {
            if (connector == null)
                return false;

            Connection connection = null; PreparedStatement statement = null;
            try {
                connection = connector.connection(); statement = connection.prepareStatement("UPDATE " + USERS_TABLE + " SET premium = ? WHERE uniqueID = ?;");
                statement.setBoolean(1, premium); statement.setString(2, uniqueID.toString()); return statement.executeUpdate() != 0;
            } catch (SQLException exception) {
                exception.printStackTrace(); return false;
            } finally {
                DataSourceUtils.close(connection, statement, null);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteUser(UUID uniqueID) {
        return CompletableFuture.supplyAsync(() -> {
            if (connector == null)
                return false;

            Connection connection = null; PreparedStatement statement = null;
            try {
                connection = connector.connection(); statement = connection.prepareStatement("DELETE FROM " + USERS_TABLE + " WHERE uniqueID = ?;");
                statement.setString(1, uniqueID.toString()); return statement.executeUpdate() != 0;
            } catch (SQLException exception) {
                exception.printStackTrace(); return false;
            } finally {
                DataSourceUtils.close(connection, statement, null);
            }
        });
    }
}
