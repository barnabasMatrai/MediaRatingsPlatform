import restserver.server.Server;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
//        IUserRepository repository = UserRepository.getInstance();
//        IUserService userService = UserService.getInstance(repository);
//
//        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
//        server.createContext("/users", new UserHandler(userService));
//        server.setExecutor(null);
        new Server().start();
    }
}