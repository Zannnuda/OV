package com.javamentor.qa.platform.dao.search.bridge;

import com.javamentor.qa.platform.models.entity.question.answer.VoteAnswer;
import org.hibernate.search.engine.backend.document.DocumentElement;
import org.hibernate.search.engine.backend.document.IndexFieldReference;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaElement;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.bridge.PropertyBridge;
import org.hibernate.search.mapper.pojo.bridge.binding.PropertyBindingContext;
import org.hibernate.search.mapper.pojo.bridge.mapping.programmatic.PropertyBinder;
import org.hibernate.search.mapper.pojo.bridge.runtime.PropertyBridgeWriteContext;

import java.util.List;

public class VotesAnswerCountBinder implements PropertyBinder {
    @Override
    public void bind(PropertyBindingContext context) {
        context.dependencies()
                .use("answer.voteAnswers");

        IndexSchemaElement schemaElement = context.indexSchemaElement();

        IndexFieldReference<Long> votesAnswerCountField = schemaElement
                .field("votesCount",
                        f -> f.asLong()
                                .projectable(Projectable.YES)
                                .sortable(Sortable.YES))
                .toReference();

        context.bridge(List.class, new Bridge(votesAnswerCountField));
    }

    private static class Bridge implements PropertyBridge<List> {
        private final IndexFieldReference<Long> votesAnswerCountFieldReference;

        public Bridge(IndexFieldReference<Long> votesAnswerCountFieldReference) {
            this.votesAnswerCountFieldReference = votesAnswerCountFieldReference;
        }

        @Override
        public void write(DocumentElement documentElement, List list, PropertyBridgeWriteContext propertyBridgeWriteContext) {
            List<VoteAnswer> voteAnswers = (List<VoteAnswer>) list;
            Long indexedValue = voteAnswers.stream().mapToLong(VoteAnswer::getVote).sum();
            documentElement.addValue(this.votesAnswerCountFieldReference, indexedValue);
        }

    }
}