package me.xfl03.morecrashinfo.modlauncher;

import cpw.mods.cl.JarModuleFinder;
import cpw.mods.jarhandling.SecureJar;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

public class ClasspathHelper {
    public static void add(ClassLoader classLoader, Path file) throws Throwable {
        try {
            Class<?> classLoaderClass = Class.forName("java.net.URLClassLoader");
            if (classLoaderClass.isInstance(classLoader)) {
                Method addURLMethod = classLoaderClass.getDeclaredMethod("addURL", URL.class);
                addURLMethod.setAccessible(true);
                addURLMethod.invoke(classLoader, file.toUri().toURL());
                TransformerService.logger.info("Added {} to {}", file, classLoader);
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
//                System.out.println("1 " + classLoader + ucpFieldOffset);
                Object ucpObject = unsafe.getObject(classLoader, ucpFieldOffset);
                if (ucpObject == null) {
                    //PlatformClassLoader
                    //Get IMPL_LOOKUP
                    Field implLookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
                    MethodHandles.Lookup lookup = (MethodHandles.Lookup)
                            unsafe.getObject(unsafe.staticFieldBase(implLookupField),
                                    unsafe.staticFieldOffset(implLookupField));

                    //Invoke loadModule
                    Class<?> moduleReferenceClass = Class.forName("java.lang.module.ModuleReference");
                    MethodHandle mh = lookup.findVirtual(classLoaderClass, "loadModule",
                            MethodType.methodType(void.class, moduleReferenceClass));
//                    mh.bindTo(classLoader);
                    JarModuleFinder finder = JarModuleFinder.of(SecureJar.from(file));
                    finder.findAll().forEach(it -> tryInvoke(mh, Arrays.asList(classLoader, it)));
                    TransformerService.logger.info("Added {} to {}", file, classLoader);
                    return;
//                    throw new IllegalStateException("ucp is null");
                }
                Class<?> classPathClass = Class.forName("jdk.internal.loader.URLClassPath");
                Field pathField = classPathClass.getDeclaredField("path");
                long pathFieldOffset = unsafe.objectFieldOffset(pathField);
//                System.out.println("2 " + ucpObject + pathFieldOffset);
                // noinspection ALL
                ArrayList<URL> path = (ArrayList<URL>) unsafe.getObject(ucpObject, pathFieldOffset);
                Field urlsField = findDeclaredField(classPathClass, "urls", "unopenedUrls");
                long urlsFieldOffset = unsafe.objectFieldOffset(urlsField);
//                System.out.println("3 " + ucpObject + urlsFieldOffset);
                // noinspection ALL
                Collection<URL> urls = (Collection<URL>) unsafe.getObject(ucpObject, urlsFieldOffset);
                // noinspection ALL
                synchronized (urls) {
                    if (!path.contains(file.toUri().toURL())) {
                        urls.add(file.toUri().toURL());
                        path.add(file.toUri().toURL());
                    }
                }
                TransformerService.logger.info("Added {} to {}", file, classLoader);
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

    private static void tryInvoke(MethodHandle method, List<Object> args) {
        try {
            method.invokeWithArguments(args);
        } catch (Throwable e) {
            TransformerService.logger.warn("Error invoke method: {}", e.toString());
        }
    }
}