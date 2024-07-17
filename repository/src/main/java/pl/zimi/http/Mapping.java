package pl.zimi.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.zimi.repository.annotation.Descriptor;

@AllArgsConstructor
@Getter
public class Mapping {

    String variableName;
    Descriptor descriptor;
}
