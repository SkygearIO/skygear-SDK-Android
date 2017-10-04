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
 * The change password request.
 */
public class ChangePasswordRequest extends Request {
    /**
     * Instantiates a change password request.
     *
     * @param newPassword the new password
     * @param oldPassword the old password
     */
    public ChangePasswordRequest(String newPassword, String oldPassword) {
        super("auth:password");

        this.data = new HashMap<>();

        this.data.put("password", newPassword);
        this.data.put("old_password", oldPassword);
    }

    @Override
    protected void validate() throws Exception {
        String password = (String) this.data.get("password");

        if (password == null) {
            throw new InvalidParameterException("New password should not be null");
        }

        if (password == "") {
            throw new InvalidParameterException("New password should not be empty");
        }
    }
}
