package com.danielfrak.code.springdoc.openapi.externalizeddocs.dummies;

import com.danielfrak.code.springdoc.openapi.externalizeddocs.ExternalizedSchema;
import io.swagger.v3.oas.annotations.media.Schema;

@ExternalizedSchema(source = DummyObject.class, schema = @Schema(
        description = "External description"
))
public class DummyWithExternalSchema {
}
