import handlers.UserHandler;
import repositories.IUserRepository;
import repositories.UserRepository;
import services.IUserService;
import services.UserService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        IUserRepository repository = UserRepository.getInstance();
        IUserService userService = UserService.getInstance(repository);

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/user", new UserHandler(userService));
        server.setExecutor(null);
        server.start();
    }
}