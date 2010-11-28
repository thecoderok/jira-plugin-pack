package com.googlecode.jsu.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation is used for marking fields as container for transient field in validators,
 * conditions and post-function.
 * 
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id: TransientVariable.java 38 2009-11-16 20:55:43Z lonbinder $
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface TransientVariable {
	String value() default "";
}
