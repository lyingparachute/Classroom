package com.example.classroom.auth.validation.email;

import com.example.classroom.user.email.EmailChangeRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailMatchesValidator implements ConstraintValidator<EmailMatches, Object> {
    @Override
    public void initialize(final EmailMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Object obj,
                           final ConstraintValidatorContext context) {
        final var request = (EmailChangeRequest) obj;
        final var isValid = request.getEmail().equals(request.getMatchingEmail());
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("matchingEmail").addConstraintViolation();
        }
        return isValid;
    }
}
