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
 * The Login request.
 */
public class LoginRequest extends Request {
    /**
     * Instantiates a new Login request.
     *
     * @param authData the unique identifier of a user
     * @param password the password
     */
    public LoginRequest(Map authData, String password) {
        super("auth:login");

        this.data = new HashMap<>();

        this.data.put("auth_data", authData);
        this.data.put("password", password);
    }

    @Override
    protected void validate() throws Exception {
        Map authData = (Map) this.data.get("auth_data");
        String password = (String) this.data.get("password");

        if (authData == null) {
            throw new InvalidParameterException("Auth data should not be null");
        }

        if (authData.isEmpty()) {
            throw new InvalidParameterException("Auth data should not be empty");
        }

        if (password == null) {
            throw new InvalidParameterException("Password should not be null");
        }
    }
}
