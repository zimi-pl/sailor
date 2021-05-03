package pl.zimi.repository.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.zimi.repository.annotation.Queryable;

@Queryable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityExample {

    private String abc;
    private String def;
    private Bar bar;
    private String test;
    private Integer value;

}
