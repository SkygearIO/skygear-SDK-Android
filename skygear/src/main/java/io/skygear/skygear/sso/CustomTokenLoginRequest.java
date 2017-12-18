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

package io.skygear.skygear.sso;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import io.skygear.skygear.Request;

/**
 * The Login with custom token request.
 */
public class CustomTokenLoginRequest extends Request {
    /**
     * Instantiates a new Login with custom token request.
     *
     * @param token the token string
     */
    public CustomTokenLoginRequest(String token) {
        super("sso:custom_token:login");

        this.data = new HashMap<>();
        data.put("token", token);
    }

    @Override
    protected void validate() throws Exception {
        String token = (String) this.data.get("token");

        if (token == null) {
            throw new InvalidParameterException("Token should not be null");
        }
    }
}
