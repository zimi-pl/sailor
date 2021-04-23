package pl.zimi.repository;

import java.lang.reflect.Field;

class Manipulator {
    static Object get(final Object source, final String path) {
        Object deeperSource = source;
        try {
            for (final String part : path.split("\\.")) {
                final Field field = deeperSource.getClass().getDeclaredField(part);
                field.setAccessible(true);
                deeperSource = field.get(deeperSource);
            }
            return deeperSource;
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
