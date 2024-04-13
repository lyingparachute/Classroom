package com.example.classroom.auth.validation.email;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = EmailMatchesValidator.class)
@Target({ElementType.TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface EmailMatches {
    String message() default "Emails don't match.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
