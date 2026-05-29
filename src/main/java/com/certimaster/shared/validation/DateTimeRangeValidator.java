package com.certimaster.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class DateTimeRangeValidator implements ConstraintValidator<ValidDateTimeRange, Object> {

    private static final Logger log = LoggerFactory.getLogger(DateTimeRangeValidator.class);
    
    private String fromFieldName;
    private String toFieldName;

    @Override
    public void initialize(ValidDateTimeRange constraintAnnotation) {
        this.fromFieldName = constraintAnnotation.from();
        this.toFieldName = constraintAnnotation.to();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            Field fromField = value.getClass().getDeclaredField(fromFieldName);
            Field toField = value.getClass().getDeclaredField(toFieldName);
            
            fromField.setAccessible(true);
            toField.setAccessible(true);
            
            LocalDateTime fromDateTime = (LocalDateTime) fromField.get(value);
            LocalDateTime toDateTime = (LocalDateTime) toField.get(value);
            
            if (fromDateTime == null || toDateTime == null) {
                return true;
            }
            
            return !fromDateTime.isAfter(toDateTime);
            
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Error validating datetime range", e);
            return false;
        }
    }
}
