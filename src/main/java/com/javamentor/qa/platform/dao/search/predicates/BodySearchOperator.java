package com.javamentor.qa.platform.dao.search.predicates;

import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BodySearchOperator extends SearchOperator {
    protected BodySearchOperator(@Value("body search operator") String description,
                                 @Value("14") int order,
                                 @Value("Поиск по описанию вопроса") String searchType,
                                 @Value("body:ищу пример Java-кода") String searchSyntax) {
        super(description, order, searchType, searchSyntax);
    }

    @Override
    public BooleanPredicateClausesStep<?> parse(StringBuilder query, SearchPredicateFactory factory, BooleanPredicateClausesStep<?> booleanPredicate) {
        Pattern pattern = Pattern.compile("(?<=body:).*");
        Matcher matcher = pattern.matcher(query);

        if (!matcher.find()) {
            return booleanPredicate;
        }

        String searchStr = matcher.group().trim();
        booleanPredicate = booleanPredicate.must(factory.match()
                .field("description")
                .matching(searchStr)
                .fuzzy(1)
                .toPredicate());

        query.replace(0, query.length(), "");

        return booleanPredicate;
    }
}
