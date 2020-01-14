package com.danielfrak.code.programmaticdocs;

import com.fasterxml.jackson.databind.type.TypeFactory;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Enables programmatic configuration of OpenAPI schema.
 * <p>
 * Example usage:
 * <p>
 * <pre>{@code
 * externalModelDocumentation
 *     .add(new ExternalModel()
 *     .source(MyValueObject.class)
 *     .implementation(String.class)
 *     .schema(new Schema<>()
 *         .example("Some example")
 *         .description("Some description")));
 * }</pre>
 */
@Component
public class ModelDocumentation implements ModelConverter {

    private Map<Class<?>, Class<?>> classMap = new HashMap<>();
    private Map<Class<?>, Schema<?>> schemaMap = new HashMap<>();

    public ModelDocumentation add(DocumentedModel model) {
        if(model.sourceClass == null) {
            throw new IllegalArgumentException("Source class cannot be NULL");
        }
        if(model.implementation != null) {
            classMap.put(model.sourceClass, model.implementation);
        }
        if(model.schema != null) {
            schemaMap.put(model.sourceClass, model.schema);
        }
        return this;
    }

    @Override
    public Schema<?> resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        Class<?> typeClass = TypeFactory.rawClass(type.getType());

        if (classMap.containsKey(typeClass)) {
            type.setType(classMap.get(typeClass));
        }

        try {
            return overrideSchema(typeClass, resolveChain(type, context, chain));
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not convert model", e);
        }
    }

    private Schema<?> resolveChain(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        if (chain.hasNext()) {
            return chain.next().resolve(type, context, chain);
        } else {
            return null;
        }
    }

    private Schema<?> overrideSchema(Class<?> typeName, Schema<?> model) throws IllegalAccessException {
        if (model == null || !schemaMap.containsKey(typeName)) {
            return model;
        }

        Schema<?> schema = schemaMap.get(typeName);

        for(Field field: schema.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (isBlank(field.get(model))) {
                field.set(model, field.get(schema));
            }
        }

        return model;
    }

    private boolean isBlank(Object modelValue) {
        return modelValue == null || (modelValue instanceof String && StringUtils.isBlank((String) modelValue));
    }

    public static class DocumentedModel {

        private Class<?> sourceClass;
        private Class<?> implementation;
        private Schema<?> schema;

        /**
         * The class to document
         */
        public DocumentedModel source(Class<?> sourceClass) {
            this.sourceClass = sourceClass;
            return this;
        }

        /**
         * Programmatic alternative to {@code @Schema(implementation = X)}
         */
        public DocumentedModel implementation(Class<?> implementation) {
            this.implementation = implementation;
            return this;
        }

        /**
         * A base schema for the class. Its values may be overridden by annotations.
         */
        public DocumentedModel schema(Schema<?> schema) {
            this.schema = schema;
            return this;
        }
    }
}
