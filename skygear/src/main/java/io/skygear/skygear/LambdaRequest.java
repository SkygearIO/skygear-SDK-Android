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
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Skygear Lambda Function Request.
 */
public class LambdaRequest extends Request {
    private static Set<? extends Class> CompatibleValueClasses = new HashSet<>(Arrays.asList(
            Boolean.class,
            Byte.class,
            Character.class,
            Double.class,
            Float.class,
            Integer.class,
            Long.class,
            Short.class,
            String.class,
            JSONObject.class,
            JSONArray.class
    ));

    /**
     * Instantiates a new Skygear Lambda Function Request.
     *
     * @param name the name
     * @param args the args
     */
    public LambdaRequest(String name, Object[] args) {
        super(name);

        this.data = new HashMap<>();

        if (args != null) {
            for (int idx = 0; idx < args.length; idx++) {
                if (!this.isCompatibleArgument(args[idx])) {
                    throw new InvalidParameterException(
                            String.format("Argument at index %d is incompatible", idx)
                    );
                }
            }

            List<Object> argList = Arrays.asList(args);
            this.data.put("args", new JSONArray(argList));
        }
    }

    /**
     * Instantiates a new Skygear Lambda Function Request.
     *
     * @param name the name
     * @param args the args
     */
    public LambdaRequest(String name, Map<String, Object> args) {
        super(name);

        this.data = new HashMap<>();

        if (args != null) {
            for (String key : args.keySet()) {
                if (!this.isCompatibleArgument(args.get(key))) {
                    throw new InvalidParameterException(
                            String.format("Argument at index %s is incompatible", key)
                    );
                }
            }
            this.data.put("args", args);
        }
    }

    private boolean isCompatibleArgument(Object arg) {
        return arg == null || LambdaRequest.CompatibleValueClasses.contains(arg.getClass());
    }
}
