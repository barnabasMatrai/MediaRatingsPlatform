package Contexts;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import java.sql.*;
import java.util.Properties;

public abstract class Handler implements HttpHandler {
    protected static Connection getConnection() throws SQLException {
        final String url = "jdbc:postgresql://<node ip address>:5432,<node ip address>:5432/postgres?targetServerType=primary";
        final Properties props = new Properties();
        props.setProperty("user", "postgresql");
        props.setProperty("password", "password");

        return DriverManager.getConnection(url, props);
    }

    protected static void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null) return result;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }
}
