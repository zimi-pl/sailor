package pl.zimi.testing;

import pl.zimi.repository.annotation.Queryable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Queryable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Foo {

    private String abc;
    private String def;
    private String test;
    private Integer value;

}
