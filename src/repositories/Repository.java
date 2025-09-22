package repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public abstract class Repository {
    protected final String url = "jdbc:postgresql://localhost/mrp";
    protected Properties props;

    protected Properties getProperties() {
        if (props != null) {
            return props;
        }

        this.props = new Properties();
        this.props.setProperty("user", "postgres");
        this.props.setProperty("password", "password");

        return props;
    }

    protected static Connection getConnection() throws SQLException {
        final String url = "jdbc:postgresql://localhost/mrp";
        final Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "password");

        return DriverManager.getConnection(url, props);
    }
}
