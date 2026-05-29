package com.certimaster.shared.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    private static final Logger log = LoggerFactory.getLogger(DateRangeValidator.class);
    
    private String fromFieldName;
    private String toFieldName;

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
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
            
            LocalDate fromDate = (LocalDate) fromField.get(value);
            LocalDate toDate = (LocalDate) toField.get(value);
            
            if (fromDate == null || toDate == null) {
                return true;
            }
            
            return !fromDate.isAfter(toDate);
            
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Error validating date range", e);
            return false;
        }
    }
}
