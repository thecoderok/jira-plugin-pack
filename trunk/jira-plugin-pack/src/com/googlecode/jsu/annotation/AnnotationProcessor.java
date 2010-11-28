package com.googlecode.jsu.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id: AnnotationProcessor.java 38 2009-11-16 20:55:43Z lonbinder $
 */
public class AnnotationProcessor {
	private List<AbstractVisitor> visitors = new ArrayList<AbstractVisitor>();
	
	public void addVisitor(AbstractVisitor visitor) {
		this.visitors.add(visitor);
	}
	
	public void processAnnotations(Object object) {
		Class<?> clazz = object.getClass();
		
		for (Field field : clazz.getDeclaredFields()) {
			for (AbstractVisitor visitor : visitors) {
				Annotation a = field.getAnnotation(visitor.getAnnotation());
				
				if (a != null) {
					visitor.visitField(object, field, a);
				}
			}
		}
	}
}
