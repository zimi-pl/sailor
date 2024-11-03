package pl.zimi.repository;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class RepresentationBuilder {

    static Map<String, AttributeValue> build(Object object) {
        try {
            Map<String, AttributeValue> map = new HashMap<>();
            for (final Field field : object.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (String.class.equals(field.getType())) {
                    Object o = field.get(object);
                    if (o != null) {
                        map.put(field.getName(), AttributeValue.builder().s((String) o).build());
                    }
                } else if (Integer.class.equals(field.getType()) || int.class.equals(field.getType())) {
                    Object o = field.get(object);
                    if (o != null) {
                        map.put(field.getName(), AttributeValue.builder().n(Integer.toString((Integer) o)).build());
                    }
                } else if (Instant.class.equals(field.getType())) {
                    Instant instant = (Instant)(field.get(object));
                    if (instant != null) {
                        map.put(field.getName(), AttributeValue.builder().s(instant.toString()).build());
                    }
                } else {
                    Object o = field.get(object);
                    if (o != null) {
                        map.put(field.getName(), AttributeValue.builder().m(build(o)).build());
                    }
                }
            }
            return map;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
