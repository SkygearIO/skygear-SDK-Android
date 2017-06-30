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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Query Predicate Helper Class.
 */
class QueryPredicate {
    /**
     * Get a keypath representation of a record attribute key.
     *
     * @param key the key
     * @return the json object
     * @throws JSONException the json exception
     */
    static JSONObject keypathRepresentation(String key) throws JSONException {
        JSONObject keypath = new JSONObject();
        keypath.put("$type", "keypath");
        keypath.put("$val", key);

        return keypath;
    }

    /**
     * Get a basic predicate.
     *
     * @param leftOperand  the left operand
     * @param operator     the operator
     * @param rightOperand the right operand
     * @return the predicate
     */
    static JSONArray basicPredicate(Object leftOperand, String operator, Object rightOperand) {
        JSONArray predicateJson = new JSONArray();
        predicateJson.put(operator);
        predicateJson.put(leftOperand);
        predicateJson.put(rightOperand);

        return predicateJson;
    }

    /**
     * Create a function typed predicate.
     *
     * @param functionName the function name
     * @param args         the function argument
     * @return the predicate
     */
    static JSONArray functionPredicate(String functionName, Object[] args) {
        JSONArray predicate = new JSONArray();
        predicate.put("func");
        predicate.put(functionName);

        if (args != null) {
            for (int idx = 0; idx < args.length; idx++) {
                Object perArg = args[idx];
                predicate.put(perArg);
            }
        }

        return predicate;
    }

    /**
     * Get an inverted predicate.
     *
     * @param anotherJSON the original predicate
     * @return the predicate
     */
    static JSONArray not(JSONArray anotherJSON) {
        JSONArray predicateJson = new JSONArray();
        predicateJson.put("not");
        predicateJson.put(anotherJSON);

        return predicateJson;
    }
}
