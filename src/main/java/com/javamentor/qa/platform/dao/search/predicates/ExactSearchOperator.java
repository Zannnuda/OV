package com.javamentor.qa.platform.dao.search.predicates;

import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ExactSearchOperator extends SearchOperator {
    protected ExactSearchOperator(@Value("exact search operator") String description,
                                  @Value("6") int order,
                                  @Value("Поиск по точному совпадению") String searchType,
                                  @Value("\"Слова в кавычках\"") String searchSyntax) {
        super(description, order, searchType, searchSyntax);
    }

    @Override
    public BooleanPredicateClausesStep<?> parse(StringBuilder query, SearchPredicateFactory factory, BooleanPredicateClausesStep<?> booleanPredicate) {
        Pattern pattern = Pattern.compile("(?<=\").*(?=\")");
        Matcher matcher = pattern.matcher(query);

        if (!matcher.find()) {
            return booleanPredicate;
        }

        booleanPredicate = booleanPredicate.must(factory.phrase()
                .fields("title", "description", "htmlBody")
                .matching(matcher.group())
                .toPredicate());

        query.replace(0, query.length(), "");

        return booleanPredicate;
    }
}
