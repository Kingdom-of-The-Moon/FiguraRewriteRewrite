package org.moon.figura.lua.docs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Documentation for a type we add to lua
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LuaTypeDoc {

    /**
     * Returns a name to be used for this type.
     */
    String name() default "";

    /**
     * Returns a translation key for the description of this type.
     */
    String value();

    boolean whitelist() default false;

}
