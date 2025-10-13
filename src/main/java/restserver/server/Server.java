package restserver.server;

import com.sun.net.httpserver.HttpServer;
import service.UserHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 10);

        server.createContext("/users", new UserHandler());

        server.start();
    }
}
