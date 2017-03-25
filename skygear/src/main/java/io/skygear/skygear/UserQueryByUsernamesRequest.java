package io.skygear.skygear;

import java.security.InvalidParameterException;
import java.util.HashMap;

/**
 * The Skygear User Query Request.
 */
public class UserQueryByUsernamesRequest extends Request {
    /**
     * Instantiates a new User Query Request.
     *
     * @param usernames the usernames
     */
    public UserQueryByUsernamesRequest(String[] usernames) {
        super("user:query");

        this.data = new HashMap<>();
        this.data.put("usernames", usernames);
    }

    @Override
    protected void validate() throws Exception {
        String[] usernames = (String[]) this.data.get("usernames");

        if (usernames.length == 0) {
            throw new InvalidParameterException("No usernames to query");
        }
    }
}
