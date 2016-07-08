package io.skygear.skygear;

import java.security.InvalidParameterException;
import java.util.HashMap;

/**
 * The Skygear User Query Request.
 */
public class UserQueryRequest extends Request {
    /**
     * Instantiates a new User Query Request.
     *
     * @param emails the emails
     */
    public UserQueryRequest(String[] emails) {
        super("user:query");

        this.data = new HashMap<>();
        this.data.put("emails", emails);
    }

    @Override
    protected void validate() throws Exception {
        String[] emails = (String[]) this.data.get("emails");

        if (emails.length == 0) {
            throw new InvalidParameterException("No emails to query");
        }
    }
}
