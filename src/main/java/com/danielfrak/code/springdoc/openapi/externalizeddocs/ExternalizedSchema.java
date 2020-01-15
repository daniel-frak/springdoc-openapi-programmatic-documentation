package com.danielfrak.code.springdoc.openapi.externalizeddocs;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.*;

@Configuration
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExternalizedSchema {

    Class<?> source();
    Schema schema();
}
