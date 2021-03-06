package org.moon.figura.lua.docs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LuaFunctionOverload {

    /**
     * The types of the arguments in this overload.
     */
    Class<?>[] argumentTypes() default {};

    /**
     * The names to give to the arguments in this overload.
     */
    String[] argumentNames() default {};

    Class<?> returnType() default DEFAULT.class;

    static final class DEFAULT {}
}
