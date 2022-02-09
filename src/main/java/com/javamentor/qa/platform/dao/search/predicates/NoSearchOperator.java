package com.javamentor.qa.platform.dao.search.predicates;

import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.predicate.dsl.MatchPredicateOptionsStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NoSearchOperator extends SearchOperator {

    public NoSearchOperator(@Value("clean query string without search operators") String description,
                            @Value("100") int order,  // этот оператор должен быть последним, всегда.
                            @Value("Простой поиск") String searchType,
                            @Value("Простой поисковый запрос") String searchSyntax) {
        super(description, order, searchType, searchSyntax);
    }

    @Override
    public BooleanPredicateClausesStep<?> parse(StringBuilder query, SearchPredicateFactory factory, BooleanPredicateClausesStep<?> booleanPredicate) {

        if (query.toString().trim().isEmpty()) {
            return booleanPredicate;
        }

        booleanPredicate = booleanPredicate.must(factory.match()
                .fields("description", "title", "htmlBody")
                .matching(query.toString())
                .fuzzy(1)
                .toPredicate());

        query.replace(0, query.length(), "");

        return booleanPredicate;
    }
}
