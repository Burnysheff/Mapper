package ru.hse.homework4.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({
    ElementType.RECORD_COMPONENT,
    ElementType.FIELD,
})
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignored {
}
