package pl.zimi.repository.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class AssertionResult {

    private boolean passed;
    private String message;
}
