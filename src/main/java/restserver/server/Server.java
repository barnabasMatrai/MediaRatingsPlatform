package restserver.server;

import com.sun.net.httpserver.HttpServer;
import service.IUserService;
import service.UserService;
import repository.repository.IUserRepository;
import repository.repository.UserRepository;
import handler.UserHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 10);

        IUserRepository userRepository = UserRepository.getInstance();
        IUserService userController = UserService.getInstance(userRepository);
        server.createContext("/api/users", new UserHandler(userController));

        server.start();
    }
}
