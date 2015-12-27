package info.limpet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Provides UI metadata for Java bean getter methods
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface UIProperty
{
	static String CATEGORY_LABEL = "Label";
	static String CATEGORY_METADATA = "Metadata";
	static String CATEGORY_CALCULATION = "Calculation";

	/**
	 * @return user-friendly name of this property that will be displayed in the
	 *         UI
	 */
	String name();

	String category();

	int min() default Integer.MIN_VALUE;

	int max() default Integer.MAX_VALUE;

	/**
	 * @return default value for integer properties
	 */
	int defaultInt() default 0;

	/**
	 * @return default value for boolean properties
	 */
	boolean defaultBoolean() default false;

	/**
	 * @return default value for String properties
	 */
	String defaultString() default "";
}
