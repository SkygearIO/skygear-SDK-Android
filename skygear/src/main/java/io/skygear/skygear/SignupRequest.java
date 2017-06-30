/*
 * Copyright 2017 Oursky Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.skygear.skygear;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

/**
 * The Sign up request.
 */
public class SignupRequest extends Request {
    final boolean anonymous;

    /**
     * Instantiates a new sign up request.
     *
     * @param username the username
     * @param email    the email
     * @param password the password
     */
    public SignupRequest(String username, String email, String password) {
        super("auth:signup");

        this.anonymous = false;
        this.data = new HashMap<>();

        this.data.put("username", username);
        this.data.put("email", email);
        this.data.put("password", password);
    }

    /**
     * Instantiates a new anonymous user sign up request.
     */
    public SignupRequest() {
        super("auth:signup");

        this.anonymous = true;
        this.data = new HashMap<>();
    }

    @Override
    protected void validate() throws Exception {
        if (this.anonymous) {
            return;
        }

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
