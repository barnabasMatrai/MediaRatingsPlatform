package service;

import java.security.SecureRandom;
import java.util.Base64;

public class AuthenticationService {
    private static AuthenticationService instance;
    private final SecureRandom random;
    private final Base64.Encoder encoder;

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

    public String generateToken() {
        byte[] randomBytes = new byte[24];
        random.nextBytes(randomBytes);
        return encoder.encodeToString(randomBytes);
    };
}
