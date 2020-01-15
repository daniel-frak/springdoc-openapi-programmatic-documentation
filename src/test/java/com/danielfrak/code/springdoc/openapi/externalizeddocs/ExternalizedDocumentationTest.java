package com.danielfrak.code.springdoc.openapi.externalizeddocs;

import com.danielfrak.code.springdoc.openapi.externalizeddocs.dummies.DummyObject;
import com.danielfrak.code.springdoc.openapi.externalizeddocs.dummies.DummyWithExternalSchema;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Iterator;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExternalizedDocumentationTest {

    @Mock
    private ModelConverterContext modelConverterContext;

    @Mock
    private ApplicationContext applicationContext;

    @Test
    void appliesExternalSchema() {
        setupApplicationContextWithAnnotatedBean();
        ExternalizedDocumentation externalizedDocumentation = new ExternalizedDocumentation(applicationContext);

        Iterator<ModelConverter> chain =
                singletonList((ModelConverter) new ModelResolver(new ObjectMapper())).iterator();

        Schema<?> schema = externalizedDocumentation.resolve(new AnnotatedType(DummyObject.class),
                modelConverterContext, chain);

        assertEquals("External description", schema.getDescription());
    }

    private void setupApplicationContextWithAnnotatedBean() {
        HashMap<String, Object> beanMap = new HashMap<>();
        beanMap.put("dummy", new DummyWithExternalSchema());
        when(applicationContext.getBeansWithAnnotation(ExternalizedSchema.class))
                .thenReturn(beanMap);
        when(applicationContext.findAnnotationOnBean("dummy", ExternalizedSchema.class))
                .thenReturn(DummyWithExternalSchema.class.getAnnotation(ExternalizedSchema.class));
    }
}