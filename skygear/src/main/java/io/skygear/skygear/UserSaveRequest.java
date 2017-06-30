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

import java.util.HashMap;

/**
 * The Skygear User Save Request.
 */
public class UserSaveRequest extends Request {
    /**
     * Instantiates a new Skygear User Save Request.
     *
     * @param user the user
     */
    public UserSaveRequest(User user) {
        super("user:update");

        this.data = new HashMap<>();
        this.data.put("_id", user.getId());

        String email = user.getEmail();
        if (email != null) {
            this.data.put("email", email);
        }

        Role[] roles = user.getRoles();
        String[] roleNames = new String[roles.length];
        for (int idx = 0; idx < roles.length; idx++) {
            roleNames[idx] = roles[idx].getName();
        }

        this.data.put("roles", roleNames);
    }
}
