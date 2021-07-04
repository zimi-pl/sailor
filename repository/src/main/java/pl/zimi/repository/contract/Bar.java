package pl.zimi.repository.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.zimi.repository.annotation.Queryable;

@Queryable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Bar {

    private String str;
}
