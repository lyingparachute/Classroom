package com.example.classroom.auth.validation.password;

import com.example.classroom.user.password.PasswordRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public void initialize(final PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(final Object obj,
                           final ConstraintValidatorContext context) {
        final var request = (PasswordRequest) obj;
        final var isValid = request.getPassword().equals(request.getMatchingPassword());
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("matchingPassword").addConstraintViolation();
        }
        return isValid;
    }
}
