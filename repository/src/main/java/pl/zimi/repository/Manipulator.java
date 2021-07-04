package pl.zimi.repository;

import pl.zimi.repository.annotation.Descriptor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class Manipulator {

    public static Value get(final Object source, final String path) {
        Object deeperSource = source;
        try {
            for (final String part : path.split("\\.")) {
                if (deeperSource != null) {
                    final Field field = deeperSource.getClass().getDeclaredField(part);
                    field.setAccessible(true);
                    deeperSource = field.get(deeperSource);
                } else {
                    return Value.failure("problem with " + part);
                }
            }
            return Value.value(deeperSource);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void set(final T source, final String path, final Object value) {
        Object parent = source;
        Object child = source;
        try {
            final var parts = path.split("\\.");
            for (int i = 0; i < parts.length; i++) {
                final Field field = parent.getClass().getDeclaredField(parts[i]);
                field.setAccessible(true);
                if (parts.length - 1 != i) {
                    child = field.get(parent);
                } else {
                    field.set(parent, value);
                }
                parent = child;
            }
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T noArgConstructor(final Class<T> source) {
        try {
            return source.getConstructor().newInstance();
        } catch (final NoSuchMethodException|IllegalAccessException|InstantiationException|InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Class type(final Class<T> clazz, final String path) {
        Class startClazz = clazz;
        try {
            for (final String part : path.split("\\.")) {
                final Field field = startClazz.getDeclaredField(part);
                startClazz = field.getType();
            }
            return startClazz;
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
