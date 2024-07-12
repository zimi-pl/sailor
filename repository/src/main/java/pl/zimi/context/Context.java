package pl.zimi.context;

import java.time.Clock;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Context {

    private final Map<Class, Object> beans = new HashMap<>();

    public static Context create() {
        return new Context();
    }

    public <T> T getBean(Class<T> clazz) {
        if (beans.containsKey(clazz)) {
            return (T)beans.get(clazz);
        }
        try {
            final var constructor = clazz.getDeclaredConstructors()[0];
            System.out.println(constructor.getParameterCount());
            final var parameters = Arrays.stream(constructor.getParameterTypes()).map(this::getBean).collect(Collectors.toList()).toArray();
            final var t = (T) constructor.newInstance(parameters);
            beans.put(t.getClass(), t);
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void register(Class clazz, Object object) {
        beans.put(clazz, object);
    }
}
