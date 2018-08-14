package com.alany.u2.rule;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( { java.lang.annotation.ElementType.METHOD} )
public @interface Repeat {
    public abstract int times();
}