package com.javamentor.qa.platform.dao.search.predicates;

import org.hibernate.search.engine.search.predicate.SearchPredicate;
import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ExcludeSearchOperator extends SearchOperator {
    protected ExcludeSearchOperator(@Value("exclude (-) search operator") String description,
                                    @Value("5") int order,
                                    @Value("Исключить из поиска") String searchType,
                                    @Value("-яблоки") String searchSyntax) {
        super(description, order, searchType, searchSyntax);
    }

    @Override
    public BooleanPredicateClausesStep<?> parse(StringBuilder query, SearchPredicateFactory factory, BooleanPredicateClausesStep<?> booleanPredicate) {
        Pattern pattern = Pattern.compile("(-)([а-яА-Яa-zA-Z0-9\\-]+)");
        Matcher matcher = pattern.matcher(query);

        if (!matcher.find()) {
            return booleanPredicate;
        }
        String group = matcher.group(2);
        booleanPredicate = booleanPredicate.mustNot(factory.match()
                .fields("title", "description", "htmlBody")
                .matching(group)
                .toPredicate());

        query.replace(0, query.length(), "");

        return booleanPredicate;
    }
}
