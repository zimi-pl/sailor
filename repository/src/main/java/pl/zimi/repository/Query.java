package pl.zimi.repository;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Query {

    Filter filter;

    Sort sort;

    LimitOffset limitOffset;
}
