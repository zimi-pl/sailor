package pl.zimi.repository.contract;

import lombok.Builder;
import lombok.Data;
import pl.zimi.repository.annotation.Queryable;

@Queryable
@Data
@Builder
public class Bar {

    private String str;
}
