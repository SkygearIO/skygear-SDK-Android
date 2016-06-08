package io.skygear.skygear;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

/**
 * The Sign up request.
 */
public class SignupRequest extends Request {
    /**
     * Instantiates a new Sign up request.
     *
     * @param username the username
     * @param email    the email
     * @param password the password
     */
    public SignupRequest(String username, String email, String password) {
        super("auth:signup");

        this.data = new HashMap<>();

        this.data.put("username", username);
        this.data.put("email", email);
        this.data.put("password", password);
    }

    @Override
    protected void validate() throws Exception {
        String username = (String) this.data.get("username");
        String email = (String) this.data.get("email");
        String password = (String) this.data.get("password");

        if (username != null && email != null) {
            throw new InvalidParameterException("Username and email should not coexist");
        }

        if (username == null && email == null) {
            throw new InvalidParameterException("Username and email should not both be null");
        }

        String identifier = username != null ? username : email;
        if (identifier.length() == 0) {
            throw new InvalidParameterException("Username and email should not both be empty");
        }

        if (password == null) {
            throw new InvalidParameterException("Password should not be null");
        }
    }
}
