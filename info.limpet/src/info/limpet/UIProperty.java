package info.limpet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Provides UI metadata for Java bean getter methods
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface UIProperty
{
  String CATEGORY_LABEL = "Label";
  String CATEGORY_METADATA = "Metadata";
  String CATEGORY_CALCULATION = "Calculation";
  String CATEGORY_VALUE = "Value";

  /**
   * @return user-friendly name of this property that will be displayed in the UI
   */
  String name();

  String category() default "";

  /**
   * Some properties are visible when certain condition is met. The condition (<i>visibleWhen</i>)
   * is expressed in Java EXpression Language (JEXL). Standard arithmetic, boolean and comparison
   * operators can be used on number and string literals as well as properties of the annotated
   * class. <br/>
   * Example values of the <i>visibleWhen</i> expression:
   * <ul>
   * <li><code>age >= 65 && gender == 'MALE' || age >= 60 && gender == 'FEMALE'</code></li> In this
   * example the gender property can be String or Java enum type.
   * <li><code>country != null && country.equalsIgnoreCase('USA')</code></li> This example
   * illustrates the usage of method invocation on the <i>country</i> property.
   * <li><code>!empty(firstLine) && !(firstLine[size(firstLine)-1] =$ '.')</code></li> This example
   * demonstrates usage of the <i>empty</i> and <i>size</i> functions as well as array access.
   * Starts With and Ends With are also supported (<i>=^</i> and <i>=$</i>). It assumes there's a
   * property <i>firstLine</i> (array of strings) in the annotated object. The expression evaluates
   * to <code>true</code>, when the array is not empty and the last array element does not end with
   * a period.
   * </ul>
   * The full reference of JEXL is available here:
   * <a href="http://commons.apache.org/proper/commons-jexl/reference/syntax.html">http://commons.
   * apache.org/proper/commons-jexl/reference/syntax.html</a>.
   * 
   * @return a boolean expression string that must evaluate to <code>true</code> or
   *         <code>false</code>. The expression might refer to Java bean properties, for example
   *         <code>"size==1"</code> is a valid expression if the bean contains getter for a property
   *         named "size". Empty string means always visible.
   */
  String visibleWhen() default "";

  int min() default Integer.MIN_VALUE;

  int max() default Integer.MAX_VALUE;

  /**
   * @return default value for integer properties
   */
  int defaultInt() default 0;

  /**
   * @return default value for double properties
   */
  double defaultDouble() default 0.0;

  /**
   * @return default value for boolean properties
   */
  boolean defaultBoolean() default false;

  /**
   * @return default value for String properties
   */
  String defaultString() default "";
}
