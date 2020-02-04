# Springdoc OpenAPI Programmatic Documentation library

![Code Soapbox logo](readme-images/logo.png)

## Introduction

This library is an extension for *Springdoc OpenAPI* which allows for defining OpenAPI 3.0 schema using code.

However, while it was developed with *Springdoc OpenAPI* in mind, **you can apply this solution to any
Spring Boot OpenAPI library based on Spring Core**.

## Getting Started

1. Add a dependency on `springdoc-openapi-programmatic-documentation`:

```xml
<dependency>
  <groupId>com.danielfrak.code</groupId>
  <artifactId>springdoc-openapi-programmatic-documentation</artifactId>
  <version>1.0.1</version>
</dependency>
```

2. Inject a `ModelDocumentation` instance in a `@configuration` class and use it to add a new schema:

```java
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.context.annotation.Configuration;
import com.danielfrak.code.config.openapi.ModelDocumentation;
import com.danielfrak.code.config.openapi.ModelDocumentation.Model;
import com.danielfrak.code.model.MyValueObject;

@Configuration
public class OpenApiConfig {

    public OpenApiConfig(ModelDocumentation modelDocumentation) {
        modelDocumentation
                .add(new DocumentedModel()
                        .source(MyValueObject.class)
                        .implementation(String.class)
                        .schema(new Schema<>()
                                .example("Example value")
                                .description("Base description")));
    }
}
```

## DocumentedModel fields

* `source` - the class to document
* `implementation` - equivalent to the `implementation` on `@io.swagger.v3.oas.annotations.media.Schema`
* `schema` - an `io.swagger.v3.oas.models.media.Schema` instance to serve as the final schema for the source class