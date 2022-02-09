package com.javamentor.qa.platform.dao.search.predicates;

import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AuthorSearchOperator extends SearchOperator{
    protected AuthorSearchOperator(@Value("author search operator") String description,
                                   @Value("40") int order,
                                   @Value("Найти по id пользователя") String searchType,
                                   @Value("user:1234") String searchSyntax) {
        super(description, order, searchType, searchSyntax);
    }

    @Override
    public BooleanPredicateClausesStep<?> parse(StringBuilder query, SearchPredicateFactory factory, BooleanPredicateClausesStep<?> booleanPredicate) {
        Pattern pattern = Pattern.compile("(?<=user:).*([0-9])");
        Matcher matcher = pattern.matcher(query);

        if (!matcher.find()) {
            return booleanPredicate;
        }

        long userId = Long.parseLong(matcher.group().trim());

        booleanPredicate = booleanPredicate.must(factory.match()
                .field("user.id")
                .matching(userId)
                .toPredicate());

        query.replace(0, query.length(), "");

        return booleanPredicate;
    }
}
