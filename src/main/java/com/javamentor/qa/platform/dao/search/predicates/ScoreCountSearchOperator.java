package com.javamentor.qa.platform.dao.search.predicates;

import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ScoreCountSearchOperator extends SearchOperator {
    protected ScoreCountSearchOperator(@Value("answers count (answers:3) search operator") String description,
                                       @Value("20") int order,
                                       @Value("Поиск по количеству ответов") String searchType,
                                       @Value("score:3, score:0") String searchSyntax) {
        super(description, order, searchType, searchSyntax);
    }

    @Override
    public BooleanPredicateClausesStep<?> parse(StringBuilder query, SearchPredicateFactory factory, BooleanPredicateClausesStep<?> booleanPredicate) {
        Pattern pattern = Pattern.compile("(?<=score:).*([0-9])");
        Matcher matcher = pattern.matcher(query);

        if (!matcher.find()) {
            return booleanPredicate;
        }

        long scoreCount = Long.parseLong(matcher.group().trim());

        if (scoreCount == 0) {
            booleanPredicate = booleanPredicate.must(factory.match().field("answersCount").matching(scoreCount));
        } else {
            booleanPredicate = booleanPredicate.must(factory.range().field("answersCount").atLeast(scoreCount));
        }

        query.replace(0, query.length(), "");

        return booleanPredicate;
    }
}
