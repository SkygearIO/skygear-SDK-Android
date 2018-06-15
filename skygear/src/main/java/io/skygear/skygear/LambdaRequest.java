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

import java.lang.reflect.Array;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
            Map.class
    ));

    protected LambdaRequest(String name, Object args) {
        super(name);

        try {
            this.data = new HashMap<>();
            if (args != null) {
                this.data.put("args", ValueSerializer.serialize(args));
            }
        } catch (JSONException ex) {
            throw new InvalidParameterException(ex.getMessage());
        }
    }

    /**
     * Instantiates a new Skygear Lambda Function Request.
     *
     * @param name the name
     * @param args the args
     */
    public LambdaRequest(String name, Object[] args) {
        this(name, (Object)args);
    }

    /**
     * Instantiates a new Skygear Lambda Function Request.
     *
     * @param name the name
     * @param args the args
     */
    public LambdaRequest(String name, List<Object> args) {
        super(name);

        this.data = new HashMap<>();

        if (args != null) {
            for (int idx = 0; idx < args.size(); idx++) {
                if (!this.isCompatibleArgument(args.get(idx))) {
                    throw new InvalidParameterException(
                            String.format("Argument at index %d is incompatible", idx)
                    );
                }
            }

            this.data.put("args", new JSONArray(args));
        }
    }

    /**
     * Instantiates a new Skygear Lambda Function Request.
     *
     * @param name the name
     * @param args the args
     */
    public LambdaRequest(String name, Map<String, Object> args) {
        this(name, (Object)args);
    }

    private boolean isCompatibleArgument(Object[] array) {
        boolean result = true;
        int l = array.length;
        for (int i = 0; i < l && result; i++) {
            result &= isCompatibleArgument(array[i]);
        }
        return result;
    }

    private boolean isCompatibleArgument(List array) {
        boolean result = true;
        int l = array.size();
        for (int i = 0; i < l && result; i++) {
            result &= isCompatibleArgument(array.get(i));
        }
        return result;
    }

    private boolean isCompatibleArgument(Map<String, Object> map) {
        boolean result = true;
        for (Object obj: map.values()) {
            result &= isCompatibleArgument(obj);
        }
        return result;
    }

    private boolean isCompatibleArgument(JSONObject object) {
        boolean result = true;
        Iterator<String> iter = object.keys();
        while (result && iter.hasNext()) {
            try {
                result &= isCompatibleArgument(object.get(iter.next()));
            }
            catch (JSONException e) {
                result = false;
            }
        }
        return result;
    }

    private boolean isCompatibleArgument(JSONArray array) {
        boolean result = true;
        int l = array.length();
        for (int i = 0; i < l && result; i++) {
            try {
                result &= isCompatibleArgument(array.get(i));
            }
            catch (JSONException e) {
                result = false;
            }
        }
        return result;
    }


    private boolean isCompatibleArgument(Object arg) {
        if (arg == null) {
            return true;
        }

        if (arg instanceof JSONArray) {
            return isCompatibleArgument((JSONArray) arg);
        }

        if (arg instanceof JSONObject) {
            return isCompatibleArgument((JSONObject) arg);
        }

        if (arg instanceof Object[]) {
            return isCompatibleArgument((Object[])arg);
        }

        if (arg instanceof List) {
            return isCompatibleArgument((List) arg);
        }

        if (arg instanceof Map) {
            return isCompatibleArgument((Map<String, Object>) arg);
        }

        for (Class cls: LambdaRequest.CompatibleValueClasses) {
            if (cls.isInstance(arg)) {
                return true;
            }
        }

        if (arg == JSONObject.NULL) {
            return true;
        }

        return false;
    }
}
