package pl.zimi.repository.example;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.zimi.repository.annotation.Queryable;

import java.time.Instant;

@Queryable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Foo {

    private String id;
    private String abc;
    private String def;
    private Bar bar;
    private String test;
    private Integer value;
    private Integer seq;
    private Integer version;
    private Instant date;

}
