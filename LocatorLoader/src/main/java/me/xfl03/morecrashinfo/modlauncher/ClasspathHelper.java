package me.xfl03.morecrashinfo.modlauncher;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

public class ClasspathHelper {
    public static void add(ClassLoader classLoader, Path file) throws Throwable {
        try {
            Class<?> classLoaderClass = Class.forName("java.net.URLClassLoader");
            if (classLoaderClass.isInstance(classLoader)) {
                Method addURLMethod = classLoaderClass.getDeclaredMethod("addURL", URL.class);
                addURLMethod.setAccessible(true);
                addURLMethod.invoke(classLoader, file.toUri().toURL());
                return;
            }
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class<?> classLoaderClass = Class.forName("jdk.internal.loader.BuiltinClassLoader");
            if (classLoaderClass.isInstance(classLoader)) {
                Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                unsafeField.setAccessible(true);
                Unsafe unsafe = (Unsafe) unsafeField.get(null);
                Field ucpField = findDeclaredField(classLoaderClass, "ucp");
                long ucpFieldOffset = unsafe.objectFieldOffset(ucpField);
                Object ucpObject = unsafe.getObject(classLoader, ucpFieldOffset);
                Class<?> classPathClass = Class.forName("jdk.internal.loader.URLClassPath");
                Field pathField = classPathClass.getDeclaredField("path");
                long pathFieldOffset = unsafe.objectFieldOffset(pathField);
                // noinspection ALL
                ArrayList<URL> path = (ArrayList<URL>) unsafe.getObject(ucpObject, pathFieldOffset);
                Field urlsField = findDeclaredField(classPathClass, "urls", "unopenedUrls");
                long urlsFieldOffset = unsafe.objectFieldOffset(urlsField);
                // noinspection ALL
                Collection<URL> urls = (Collection<URL>) unsafe.getObject(ucpObject, urlsFieldOffset);
                // noinspection ALL
                synchronized (urls) {
                    if (!path.contains(file.toUri().toURL())) {
                        urls.add(file.toUri().toURL());
                        path.add(file.toUri().toURL());
                    }
                }
                return;
            }
        } catch (ClassNotFoundException ignored) {
        }

        throw new UnsupportedOperationException(classLoader.getClass().getName());
    }

    private static Field findDeclaredField(Class<?> type, String... names) throws NoSuchFieldException {
        for (Field field : type.getDeclaredFields()) {
            for (String name : names) {
                if (field.getName().equals(name)) {
                    return field;
                }
            }
        }
        Class<?> superClass = type.getSuperclass();
        if (superClass != null) {
            return findDeclaredField(type.getSuperclass(), names);
        }
        throw new NoSuchFieldException(String.join(", ", names));
    }
}