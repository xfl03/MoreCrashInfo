package me.xfl03.morecrashinfo.util;

import java.lang.reflect.Field;

public class ReflectionHelper {
    public static Object getField(Object o, String name) throws Exception {
        Class c = o.getClass();
        Field f = c.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(o);
    }
}
