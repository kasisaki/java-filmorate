package ru.yandex.practicum.filmorate.customAnnotation;

import ru.yandex.practicum.filmorate.validator.ReleaseDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
@Documented
public @interface IsAfter{
    String message() default "Wrong date";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
    String dateLimit();
}