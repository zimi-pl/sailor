package pl.zimi.repository;

import pl.zimi.repository.annotation.Descriptor;
import pl.zimi.repository.manipulation.Manipulator;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.Map;

public class EntityBuilder {

    public static <T> T build(Map<String, AttributeValue> representation, Class<T> clazz) {
        T t = Manipulator.noArgConstructor(clazz);
        for (String key : representation.keySet()) {
            Descriptor descriptor = new Descriptor(null, key);
            Class fieldType = Manipulator.type(clazz, descriptor);
            if (String.class.equals(fieldType)) {
                AttributeValue attributeValue = representation.get(key);
                String s = attributeValue.s();
                Manipulator.set(t, descriptor, s);
            } else if (Integer.class.equals(fieldType) || int.class.equals(fieldType)) {
                AttributeValue attributeValue = representation.get(key);
                String s = attributeValue.n();
                Manipulator.set(t, descriptor, Integer.parseInt(s));
            } else if (Instant.class.equals(fieldType)) {
                AttributeValue attributeValue = representation.get(key);
                String s = attributeValue.s();
                Manipulator.set(t, descriptor, Instant.parse(s));
            } else {
                Object inner = build(representation.get(key).m(), fieldType);
                Manipulator.set(t, descriptor, inner);
            }
        }
        return t;
    }

}
