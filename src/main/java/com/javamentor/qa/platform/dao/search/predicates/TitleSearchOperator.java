package com.javamentor.qa.platform.dao.search.predicates;

import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TitleSearchOperator extends SearchOperator {
    protected TitleSearchOperator(@Value("title search operator") String description,
                                  @Value("15") int order,
                                  @Value("Поиск по заголовку") String searchType,
                                  @Value("title:ищу Java") String searchSyntax) {
        super(description, order, searchType, searchSyntax);
    }

    @Override
    public BooleanPredicateClausesStep<?> parse(StringBuilder query, SearchPredicateFactory factory, BooleanPredicateClausesStep<?> booleanPredicate) {
        Pattern pattern = Pattern.compile("(?<=title:).*");
        Matcher matcher = pattern.matcher(query);

        if (!matcher.find()) {
            return booleanPredicate;
        }

        String searchStr = matcher.group().trim();
        booleanPredicate = booleanPredicate.must(factory.match()
                .fields("title")
                .matching(searchStr)
                .toPredicate());

        query.replace(0, query.length(), "");

        return booleanPredicate;
    }
}
