package com.javamentor.qa.platform.dao.search.predicates;

import org.hibernate.search.engine.search.predicate.SearchPredicate;
import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.mapper.orm.scope.SearchScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PredicateFactoryBean {
    @Autowired
    private List<SearchOperator> searchOperators;

    public SearchPredicate getPredicate(SearchScope scope, String query) {
        StringBuilder queryStringBuilder = new StringBuilder(query);
        SearchPredicateFactory factory = scope.predicate();
        BooleanPredicateClausesStep<?> booleanPredicate = factory.bool();

        Collections.sort(searchOperators);

        for (SearchOperator searchOperator: searchOperators) {
            booleanPredicate = searchOperator.parse(queryStringBuilder, factory, booleanPredicate);

            if (queryStringBuilder.length() == 0 ) {
                break;
            }
        }
        return booleanPredicate.toPredicate();
    }

}
