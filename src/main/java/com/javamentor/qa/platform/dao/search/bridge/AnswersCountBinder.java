package com.javamentor.qa.platform.dao.search.bridge;

import org.hibernate.search.engine.backend.document.DocumentElement;
import org.hibernate.search.engine.backend.document.IndexFieldReference;
import org.hibernate.search.engine.backend.document.IndexObjectFieldReference;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaElement;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaObjectField;
import org.hibernate.search.engine.backend.types.IndexFieldType;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.bridge.PropertyBridge;
import org.hibernate.search.mapper.pojo.bridge.binding.PropertyBindingContext;
import org.hibernate.search.mapper.pojo.bridge.mapping.programmatic.PropertyBinder;
import org.hibernate.search.mapper.pojo.bridge.runtime.PropertyBridgeWriteContext;

import java.util.List;

public class AnswersCountBinder implements PropertyBinder {
    @Override
    public void bind(PropertyBindingContext context) {
        context.dependencies()
                .use("question.answers");

        IndexSchemaElement schemaElement = context.indexSchemaElement();

        IndexFieldReference<Long> answersCountField = schemaElement
                .field("answersCount",
                        f -> f.asLong()
                                .projectable(Projectable.YES)
                                .sortable(Sortable.YES))
                .toReference();

        context.bridge(List.class, new Bridge(answersCountField));
    }

    private static class Bridge implements PropertyBridge<List> {
        private final IndexFieldReference<Long> answersCountFieldReference;

        public Bridge(IndexFieldReference<Long> answersCountFieldReference) {
            this.answersCountFieldReference = answersCountFieldReference;
        }

        @Override
        public void write(DocumentElement documentElement, List list, PropertyBridgeWriteContext propertyBridgeWriteContext) {
            Long indexedValue = Long.valueOf(list.size());
            documentElement.addValue(this.answersCountFieldReference, indexedValue);
        }
    }
}
