package io.skygear.skygear;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
            this.data.put("args", args);
        }
    }

    private boolean isCompatibleArgument(Object arg) {
        return arg == null || LambdaRequest.CompatibleValueClasses.contains(arg.getClass());
    }

    @Override
    protected void validate() throws Exception {
        Object[] args = (Object[]) this.data.get("args");
        if (args == null) {
            return;
        }

        for (int idx = 0; idx < args.length; idx++) {
            Object arg = args[idx];
            if (!this.isCompatibleArgument(arg)) {
                throw new InvalidParameterException(
                        String.format("Argument at index %d is incompatible", idx)
                );
            }
        }
    }
}
