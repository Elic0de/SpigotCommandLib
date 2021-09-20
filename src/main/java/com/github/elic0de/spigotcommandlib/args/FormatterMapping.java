
package com.github.elic0de.spigotcommandlib.args;

import org.bukkit.Color;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import static com.github.elic0de.spigotcommandlib.args.ArgumentFormatter.*;

public class FormatterMapping {
    private static Map<Class, ArgumentFormatter> SUPPORTED_FORMATTERS = new HashMap<>();
    private static Map<Class, ArgumentFormatter> SUPPORTED_ARRAY_FORMATTERS = new HashMap<>();

    static {
        registerMapping(String.class, STRING);
        registerMapping(Boolean.class, BOOLEAN);
        registerMapping(boolean.class, BOOLEAN);
        registerMapping(Integer.class, INTEGER);
        registerMapping(int.class, INTEGER);
        registerMapping(Long.class, LONG);
        registerMapping(long.class, LONG);
        registerMapping(Short.class, SHORT);
        registerMapping(short.class, SHORT);
        registerMapping(Double.class, DOUBLE);
        registerMapping(double.class, DOUBLE);
        registerMapping(Float.class, FLOAT);
        registerMapping(float.class, FLOAT);
        registerMapping(Color.class, COLOR);
    }

    private static Class getArrayVersionOfClass(Class clazz) {
        return Array.newInstance(clazz, 0).getClass();
    }

    private static void registerMapping(Class clazz, ArgumentFormatter formatter) {
        SUPPORTED_FORMATTERS.put(clazz, formatter);
        SUPPORTED_ARRAY_FORMATTERS.put(getArrayVersionOfClass(clazz), formatter);
    }

    /**
     * Get the formatter that can format the given type.
     *
     * @param type the type to lookup
     *
     * @return the formatter used to format the given type.
     */
    public static ArgumentFormatter lookup(Class type) {
        return SUPPORTED_FORMATTERS.get(type);
    }

    /**
     * Get the formatter that can format the given type. The type
     * should be an array (but will simply return null if not) and
     * the lookup will return the formatter that can be used to
     * parse each element in the array.
     *
     * @param type the type to lookup
     *
     * @return the formatter used to format the given type.
     */
    public static ArgumentFormatter lookpArray(Class type) {
        return SUPPORTED_ARRAY_FORMATTERS.get(type);
    }
}
