package pl.zimi.repository;

import org.junit.jupiter.api.Test;
import pl.zimi.repository.example.Bar;
import pl.zimi.repository.example.Foo;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EntityBuilderTest {

    @Test
    public void verifyBuildEntity() {
        Map<String, AttributeValue> barRepresentation = new HashMap<>();
        barRepresentation.put("str", AttributeValue.builder().s("str").build());

        Map<String, AttributeValue> representation = new HashMap<>();
        representation.put("id", AttributeValue.builder().s("some-id").build());
        representation.put("abc", AttributeValue.builder().s("abc").build());
        representation.put("def", AttributeValue.builder().s("def").build());
        representation.put("test", AttributeValue.builder().s("test").build());
        representation.put("value", AttributeValue.builder().n("73").build());
        representation.put("seq", AttributeValue.builder().n("21").build());
        representation.put("version", AttributeValue.builder().n("11").build());
        representation.put("date", AttributeValue.builder().s("2024-10-26T10:00:00Z").build());
        representation.put("bar", AttributeValue.builder().m(barRepresentation).build());

        Foo result = EntityBuilder.build(representation, Foo.class);

        Bar bar = new Bar("str");
        Foo foo = new Foo("some-id", "abc", "def", bar, "test", 73, 21, 11, Instant.parse("2024-10-26T10:00:00Z"));

        assertEquals("some-id", result.getId());
        assertEquals("abc", result.getAbc());
        assertEquals("def", result.getDef());
        assertEquals("str", result.getBar().getStr());
        assertEquals("test", result.getTest());
        assertEquals(73, result.getValue());
        assertEquals(21, result.getSeq());
        assertEquals(11, result.getVersion());
        assertEquals(Instant.parse("2024-10-26T10:00:00Z"), result.getDate());

    }

}