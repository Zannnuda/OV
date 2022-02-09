package com.javamentor.qa.platform.dao.search.predicates;

import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AnswersCountSearchOperator extends SearchOperator {
    protected AnswersCountSearchOperator(@Value("answers count (answers:3) search operator") String description,
                                         @Value("20") int order,
                                         @Value("Поиск по количеству ответов") String searchType,
                                         @Value("answers:3, answer:0") String searchSyntax) {
        super(description, order, searchType, searchSyntax);
    }

    @Override
    public BooleanPredicateClausesStep<?> parse(StringBuilder query, SearchPredicateFactory factory, BooleanPredicateClausesStep<?> booleanPredicate) {
        Pattern pattern = Pattern.compile("(?<=answers:).*([0-9])");
        Matcher matcher = pattern.matcher(query);

        if (!matcher.find()) {
            return booleanPredicate;
        }

        long answersCount = Long.parseLong(matcher.group().trim());

        if (answersCount == 0) {
            booleanPredicate = booleanPredicate.must(factory.match().field("answersCount").matching(answersCount));
        } else {
            booleanPredicate = booleanPredicate.must(factory.range().field("answersCount").atLeast(answersCount));
        }

        query.replace(0, query.length(), "");

        return booleanPredicate;
    }
}
