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

/**
 * The Skygear User Role.
 */
public class Role implements Comparable<Role> {
    private final String name;

    /**
     * Instantiates a new Role.
     *
     * @param name the name
     */
    public Role(String name) {
        this.name = name;
    }

    /**
     * Gets the role name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    @Override
    public int compareTo(Role another) {
        return this.name.compareTo(another.name);
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Role && this.compareTo((Role) another) == 0;
    }
}
