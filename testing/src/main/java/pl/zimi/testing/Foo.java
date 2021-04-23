package pl.zimi.testing;

import ann.Queryable;
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
    private String bar;
    private String test;
    private Integer value;

}
