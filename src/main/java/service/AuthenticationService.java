package service;

import com.sun.net.httpserver.HttpExchange;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthenticationService {
    private static AuthenticationService instance;
    private final SecureRandom random;
    private final Base64.Encoder encoder;

    private Map<String, String> usernamesByTokens = new HashMap<>();

    private AuthenticationService() {
        this.random = new SecureRandom();
        this.encoder = Base64.getEncoder();
    }

    public static AuthenticationService getInstance() {
        if(instance == null) {
            instance = new AuthenticationService();
        }

        return instance;
    }

    public String generateToken(String username) {
        byte[] randomBytes = new byte[24];
        random.nextBytes(randomBytes);
        String token = encoder.encodeToString(randomBytes);
        usernamesByTokens.put(token, username);
        return token;
    };

    public String getCurrentUser(HttpExchange exchange) {
        List<String> authHeaders = exchange.getRequestHeaders().get("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            String prefix = "Bearer ";
            if (authHeader.startsWith(prefix)) {
                String token = authHeader.substring(prefix.length());
                return usernamesByTokens.get(token);
            }
        }
        return null;
    }
}
