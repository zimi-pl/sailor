package pl.zimi.http;

import pl.zimi.repository.annotation.Descriptor;

public class Mapping {

    String variableName;
    Descriptor descriptor;

    public Mapping(String variableName, Descriptor descriptor) {
        this.variableName = variableName;
        this.descriptor = descriptor;
    }

    public String getVariableName() {
        return variableName;
    }

    public Descriptor getDescriptor() {
        return descriptor;
    }
}
