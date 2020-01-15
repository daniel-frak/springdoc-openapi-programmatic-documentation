package com.danielfrak.code.springdoc.openapi.externalizeddocs;

import com.fasterxml.jackson.databind.type.TypeFactory;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Enables externalized documentation of OpenAPI schema
 */
@Component
public class ExternalizedDocumentation implements ModelConverter {

    private List<ExternalizedSchema> externalSchemas = new ArrayList<>();

    public ExternalizedDocumentation(ApplicationContext ctx) {
        Map<String, Object> beans = ctx.getBeansWithAnnotation(ExternalizedSchema.class);

        for (String bean : beans.keySet()) {
            externalSchemas.add(ctx.findAnnotationOnBean(bean, ExternalizedSchema.class));
        }
    }

    @Override
    public Schema<?> resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        Class<?> typeClass = TypeFactory.rawClass(type.getType());

        updateAnnotations(type, typeClass);

        if (chain.hasNext()) {
            return chain.next().resolve(type, context, chain);
        } else {
            return null;
        }
    }

    private void updateAnnotations(AnnotatedType type, Class<?> typeClass) {
        for (ExternalizedSchema externalSchema : externalSchemas) {
            if (externalSchema.source() == typeClass) {
                List<Annotation> annotations = type.getCtxAnnotations() != null ?
                        new ArrayList<>(Arrays.asList(type.getCtxAnnotations())) : new ArrayList<>();
                annotations.add(externalSchema.schema());
                type.setCtxAnnotations(annotations.toArray(new Annotation[0]));
            }
        }
    }
}
