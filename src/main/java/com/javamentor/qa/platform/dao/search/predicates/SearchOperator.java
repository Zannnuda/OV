package com.javamentor.qa.platform.dao.search.predicates;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;

@Getter
@Setter
@AllArgsConstructor
public abstract class SearchOperator implements Comparable<SearchOperator> {
    private String description;
    private int order;
    private String searchType;
    private String searchSyntax;

    public abstract BooleanPredicateClausesStep<?> parse(StringBuilder query, SearchPredicateFactory factory, BooleanPredicateClausesStep<?> booleanPredicate);

    @Override
    public int compareTo(SearchOperator searchOperator) {
        return this.order - searchOperator.getOrder();
    }
}
