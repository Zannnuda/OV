package com.javamentor.qa.platform.dao.search.bridge;

import com.javamentor.qa.platform.models.entity.question.Tag;
import org.hibernate.search.engine.backend.document.DocumentElement;
import org.hibernate.search.engine.backend.document.IndexFieldReference;
import org.hibernate.search.engine.backend.document.IndexObjectFieldReference;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaElement;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaObjectField;
import org.hibernate.search.engine.backend.types.IndexFieldType;
import org.hibernate.search.engine.backend.types.ObjectStructure;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.mapper.pojo.bridge.PropertyBridge;
import org.hibernate.search.mapper.pojo.bridge.binding.PropertyBindingContext;
import org.hibernate.search.mapper.pojo.bridge.mapping.programmatic.PropertyBinder;
import org.hibernate.search.mapper.pojo.bridge.runtime.PropertyBridgeWriteContext;

import java.util.List;

public class TagsBinder implements PropertyBinder {
    @Override
    public void bind(PropertyBindingContext context) {
        context.dependencies()
                .use("questions.tags");

        IndexSchemaElement schemaElement = context.indexSchemaElement();

        IndexSchemaObjectField tagsField = schemaElement.objectField("tags", ObjectStructure.NESTED)
                .multiValued();

        IndexFieldType<Long> tagIdFieldType = context.typeFactory()
                .asLong()
                .projectable(Projectable.YES)
                .toIndexFieldType();

        IndexFieldType<String> tagDescriptionFieldType = context.typeFactory()
                .asString()
                .projectable(Projectable.YES)
                .toIndexFieldType();

        context.bridge(List.class, new Bridge(
                tagsField.toReference(),
                tagsField.field("id", tagIdFieldType).toReference(),
                tagsField.field("name", tagDescriptionFieldType).toReference(),
                tagsField.field("description", tagDescriptionFieldType).toReference()
        ));
    }

    private static class Bridge implements PropertyBridge<List> {

        private final IndexObjectFieldReference tagsField;
        private final IndexFieldReference<Long> tagIdField;
        private final IndexFieldReference<String> tagNameField;
        private final IndexFieldReference<String> tagDescriptionField;

        public Bridge(IndexObjectFieldReference tagsField,
                      IndexFieldReference<Long> tagIdField,
                      IndexFieldReference<String> tagNameField,
                      IndexFieldReference<String> tagDescriptionField) {
            this.tagsField = tagsField;
            this.tagIdField = tagIdField;
            this.tagNameField = tagNameField;
            this.tagDescriptionField = tagDescriptionField;
        }

        @Override
        public void write(DocumentElement target, List bridgedElement, PropertyBridgeWriteContext context) {
            List<Tag> tagList = (List<Tag>) bridgedElement;
            tagList.forEach(tag -> {
                DocumentElement tags = target.addObject(this.tagsField);
                tags.addValue(this.tagIdField, tag.getId());
                tags.addValue(this.tagNameField, tag.getName());
                tags.addValue(this.tagDescriptionField, tag.getDescription());
            });
        }
    }

}
