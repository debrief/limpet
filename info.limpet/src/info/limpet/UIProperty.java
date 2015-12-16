package info.limpet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface UIProperty
{
	static String CATEGORY_LABEL = "Label";
	static String CATEGORY_METADATA = "Metadata";
	static String CATEGORY_CALCULATION = "Calculation";
	
	String name();
	String category();
	
	int min() default Integer.MIN_VALUE;
	int max() default Integer.MAX_VALUE;
}
