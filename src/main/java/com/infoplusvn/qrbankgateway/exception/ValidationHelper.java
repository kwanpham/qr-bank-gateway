package com.infoplusvn.qrbankgateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
public class ValidationHelper {

    public static final AtomicReference<String> fieldNames = new AtomicReference<>();
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> boolean isValid(T object, Class<?>... groups) {
        if (Objects.isNull(object)) {
            throw new ValidationException("Object is required not null");
        }
        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            StringBuilder fieldName = new StringBuilder();
            int index = 0;
            for (ConstraintViolation<?> constraintViolation : violations) {
                sb.append(constraintViolation.getMessage())
                        .append(StringUtils.SPACE);
                fieldName.append(constraintViolation.getPropertyPath().toString());
                if (index < violations.size() - 1)
                    fieldName.append("|");
                index++;
            }
            fieldNames.set(fieldName.toString());
            log.error("Error occur when validate class {} : {}", object.getClass().getName(), sb);
            return false;
        }
        return true;
    }
}