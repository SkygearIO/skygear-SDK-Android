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

import org.json.JSONObject;

/**
 * The Response Handling interface for Skygear Request.
 */
public interface ResponseHandling {
    /**
     * The success callback.
     *
     * @param result the result
     */
    void onSuccess(JSONObject result);

    /**
     * The error callback.
     *
     * @param error the error
     */
    void onFail(Error error);
}
