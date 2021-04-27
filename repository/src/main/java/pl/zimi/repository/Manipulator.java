package pl.zimi.repository;

import java.lang.reflect.Field;

class Manipulator {


    static Value get(final Object source, final String path) {
        Object deeperSource = source;
        try {
            for (final String part : path.split("\\.")) {
                if (deeperSource != null) {
                    final Field field = deeperSource.getClass().getDeclaredField(part);
                    field.setAccessible(true);
                    deeperSource = field.get(deeperSource);
                } else {
                    Value.failure("problem with " + part);
                }
            }
            return Value.value(deeperSource);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void set(final T source, final String path, final int value) {
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
            }
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
