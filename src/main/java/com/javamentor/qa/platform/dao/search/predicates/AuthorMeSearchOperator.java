package com.javamentor.qa.platform.dao.search.predicates;

import com.javamentor.qa.platform.models.entity.user.User;
import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AuthorMeSearchOperator extends SearchOperator {
    protected AuthorMeSearchOperator(@Value("author is me search operator") String description,
                                     @Value("41") int order,
                                     @Value("Найти мои") String searchType,
                                     @Value("user:me") String searchSyntax) {
        super(description, order, searchType, searchSyntax);
    }

    @Override
    public BooleanPredicateClausesStep<?> parse(StringBuilder query, SearchPredicateFactory factory, BooleanPredicateClausesStep<?> booleanPredicate) {
        Pattern pattern = Pattern.compile("user:me");
        Matcher matcher = pattern.matcher(query);

        if (!matcher.find()) {
            return booleanPredicate;
        }

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = currentUser.getId();

        query.replace(0, query.length(), "");

        return booleanPredicate.must(factory.match()
                .field("user.id")
                .matching(userId)
                .toPredicate());
    }
}
