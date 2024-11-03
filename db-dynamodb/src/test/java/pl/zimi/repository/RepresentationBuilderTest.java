package pl.zimi.repository;

import org.junit.jupiter.api.Test;
import pl.zimi.repository.example.Bar;
import pl.zimi.repository.example.Foo;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RepresentationBuilderTest {

    @Test
    public void shouldBuild() {
        Bar bar = new Bar("str");
        Foo foo = new Foo("some-id", "abc", "def", bar, "test", 73, 21, 11, Instant.parse("2024-10-26T10:00:00Z"));

        Map<String, AttributeValue> representation = RepresentationBuilder.build(foo);
        assertEquals(9, representation.size());
        assertEquals(AttributeValue.builder().s("some-id").build(), representation.get("id"));
        assertEquals(AttributeValue.builder().s("abc").build(), representation.get("abc"));
        assertEquals(AttributeValue.builder().s("def").build(), representation.get("def"));
        assertEquals(AttributeValue.builder().s("test").build(), representation.get("test"));
        assertEquals(AttributeValue.builder().n("73").build(), representation.get("value"));
        assertEquals(AttributeValue.builder().n("21").build(), representation.get("seq"));
        assertEquals(AttributeValue.builder().n("11").build(), representation.get("version"));
        assertEquals(AttributeValue.builder().s("2024-10-26T10:00:00Z").build(), representation.get("date"));

        assertNotNull(representation.get("bar"));
        Map<String, AttributeValue> bar1 = representation.get("bar").m();
        assertEquals(1, bar1.size());
        assertEquals(AttributeValue.builder().s("str").build(), bar1.get("str"));
    }

}