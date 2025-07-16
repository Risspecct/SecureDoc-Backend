package users.rishik.SecureDoc.Annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NullOrNotBlankValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NullOrNotBlank {
    String message() default "must be null or a non-blank string";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}