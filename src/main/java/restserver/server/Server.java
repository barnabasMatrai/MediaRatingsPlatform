package restserver.server;

import com.sun.net.httpserver.HttpServer;
import controller.IUserController;
import controller.UserController;
import repository.repository.IUserRepository;
import repository.repository.UserRepository;
import service.UserHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 10);

        IUserRepository userRepository = UserRepository.getInstance();
        IUserController userController = UserController.getInstance(userRepository);
        server.createContext("/api/users", new UserHandler(userController));

        server.start();
    }
}
