package restserver.server;

import com.sun.net.httpserver.HttpServer;
import handler.MediaHandler;
import handler.RatingHandler;
import repository.repository.*;
import service.*;
import handler.UserHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 10);

        IUserRepository userRepository = UserRepository.getInstance();
        IUserService userService = UserService.getInstance(userRepository);
        server.createContext("/api/users", new UserHandler(userService));

        IMediaRepository mediaRepository = MediaRepository.getInstance();
        IMediaService mediaService = MediaService.getInstance(mediaRepository);
        server.createContext("/api/media", new MediaHandler(mediaService));

        IRatingRepository ratingRepository = RatingRepository.getInstance();
        IRatingService ratingService = RatingService.getInstance(ratingRepository);
        server.createContext("/api/rating", new RatingHandler(ratingService));

        server.start();
    }
}
