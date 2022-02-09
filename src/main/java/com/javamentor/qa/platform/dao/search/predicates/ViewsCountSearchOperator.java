package com.javamentor.qa.platform.dao.search.predicates;

import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ViewsCountSearchOperator extends SearchOperator {
    protected ViewsCountSearchOperator(@Value("views count (answers:3) search operator") String description,
                                       @Value("22") int order,
                                       @Value("Поиск по количеству просмотров") String searchType,
                                       @Value("views:3, views:0") String searchSyntax) {
        super(description, order, searchType, searchSyntax);
    }

    @Override
    public BooleanPredicateClausesStep<?> parse(StringBuilder query, SearchPredicateFactory factory, BooleanPredicateClausesStep<?> booleanPredicate) {
        Pattern pattern = Pattern.compile("(?<=views:).*([0-9])");
        Matcher matcher = pattern.matcher(query);

        if (!matcher.find()) {
            return booleanPredicate;
        }

        int viewsCount = Integer.parseInt(matcher.group().trim());

        if (viewsCount == 0) {
            booleanPredicate = booleanPredicate.must(factory.match().field("viewCount").matching(viewsCount));
        } else {
            booleanPredicate = booleanPredicate.must(factory.range().field("viewCount").atLeast(viewsCount));
        }

        query.replace(0, query.length(), "");

        return booleanPredicate;
    }
}
