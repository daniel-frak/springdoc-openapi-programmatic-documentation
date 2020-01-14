package com.danielfrak.code.programmaticdocs;

import com.danielfrak.code.programmaticdocs.ModelDocumentation.DocumentedModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModelDocumentationTest {

    private ModelDocumentation modelDocumentation;

    @Mock
    private ModelConverterContext modelConverterContext;

    @BeforeEach
    void setUp() {
        modelDocumentation = new ModelDocumentation();
    }

    @Test
    void throwsExceptionWhenSourceClassNotProvided() {
        assertThrows(IllegalArgumentException.class, () -> modelDocumentation.add(new DocumentedModel()));
    }

    @Test
    void changesImplementation() {
        Iterator<ModelConverter> chain =
                singletonList((ModelConverter) new ModelResolver(new ObjectMapper())).iterator();

        modelDocumentation.add(new DocumentedModel()
                .source(DummyObject.class)
                .implementation(String.class));

        Schema<?> schema = modelDocumentation.resolve(new AnnotatedType(DummyObject.class), modelConverterContext,
                chain);

        assertEquals("string", schema.getType());
    }

    @Test
    void changesSchema() {
        Iterator<ModelConverter> chain =
                singletonList((ModelConverter) new ModelResolver(new ObjectMapper())).iterator();

        final String descriptionValue = "Some description";
        final String exampleValue = "Some example";

        modelDocumentation.add(new DocumentedModel()
                .source(DummyObject.class)
                .schema(new Schema<>()
                        .description(descriptionValue)
                        .example(exampleValue)));

        Schema<?> schema = modelDocumentation.resolve(new AnnotatedType(DummyObject.class), modelConverterContext,
                chain);

        assertEquals(descriptionValue, schema.getDescription());
        assertEquals(exampleValue, schema.getExample());
    }

    @Test
    void willNotChangeValueSchemaWhenAlreadyDefined() {
        ModelConverter mockConverter = mock(ModelConverter.class);
        Iterator<ModelConverter> chain = singletonList(mockConverter).iterator();

        final String exampleValue = "Some example";
        final String existingDescriptionValue = "Existing description";

        modelDocumentation.add(new DocumentedModel()
                .source(DummyObject.class)
                .schema(new Schema<>()
                        .description("Some description")
                        .example(exampleValue)));

        when(mockConverter.resolve(any(), any(), any()))
                .thenReturn(new Schema<>().description(existingDescriptionValue));

        Schema<?> schema = modelDocumentation.resolve(new AnnotatedType(DummyObject.class), modelConverterContext,
                chain);

        assertEquals(existingDescriptionValue, schema.getDescription());
        assertEquals(exampleValue, schema.getExample());
    }

    private static class DummyObject {
    }
}