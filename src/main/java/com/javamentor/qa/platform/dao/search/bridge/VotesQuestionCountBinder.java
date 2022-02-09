package com.javamentor.qa.platform.dao.search.bridge;

import com.javamentor.qa.platform.models.entity.question.VoteQuestion;
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

public class VotesQuestionCountBinder implements PropertyBinder {
    @Override
    public void bind(PropertyBindingContext context) {
        context.dependencies()
                .use("question.voteQuestions");

        IndexSchemaElement schemaElement = context.indexSchemaElement();

        IndexFieldReference<Long> votesQuestionCountField = schemaElement
                .field("votesCount",
                        f -> f.asLong()
                                .projectable(Projectable.YES)
                                .sortable(Sortable.YES))
                .toReference();

        context.bridge(List.class, new Bridge(votesQuestionCountField));
    }

    private static class Bridge implements PropertyBridge<List> {
        private final IndexFieldReference<Long> votesQuestionCountFieldReference;

        public Bridge(IndexFieldReference<Long> votesQuestionCountFieldReference) {
            this.votesQuestionCountFieldReference = votesQuestionCountFieldReference;
        }

        @Override
        public void write(DocumentElement documentElement, List list, PropertyBridgeWriteContext propertyBridgeWriteContext) {
            List<VoteQuestion> voteQuestions = (List<VoteQuestion>) list;
            Long indexedValue = voteQuestions.stream().mapToLong(VoteQuestion::getVote).sum();
            documentElement.addValue(this.votesQuestionCountFieldReference, indexedValue);
        }

    }
}
