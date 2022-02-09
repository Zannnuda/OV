package com.javamentor.qa.platform.dao.search.predicates;

import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TagSearchOperator extends SearchOperator {

    public TagSearchOperator(@Value("[tag] search operator") String description,
                            @Value("30") int order,
                             @Value("Поиск по меткам") String searchType,
                             @Value("[Название метки]") String searchSyntax) {
        super(description, order, searchType, searchSyntax);
    }

    @Override
    public BooleanPredicateClausesStep<?> parse(StringBuilder query, SearchPredicateFactory factory, BooleanPredicateClausesStep<?> booleanPredicate) {
        Pattern pattern = Pattern.compile("(?<=\\[).*(?=\\])");
        Matcher matcher = pattern.matcher(query);

        if (!matcher.find()) {
            return booleanPredicate;
        }

        booleanPredicate = booleanPredicate.must(factory.match()
                .fields("tags.name", "question.tags.name")
                .matching(matcher.group())
                .fuzzy(1)
                .toPredicate());

        query.replace(0, query.length(), "");

        return booleanPredicate;
    }
}
