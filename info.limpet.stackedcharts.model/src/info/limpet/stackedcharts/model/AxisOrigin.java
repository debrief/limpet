/**
 */
package info.limpet.stackedcharts.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Axis Origin</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAxisOrigin()
 * @model
 * @generated
 */
public enum AxisOrigin implements Enumerator
{
  /**
   * The '<em><b>Min</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #MIN_VALUE
   * @generated
   * @ordered
   */
  MIN(0, "Min", "Min"),

  /**
   * The '<em><b>Max</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #MAX_VALUE
   * @generated
   * @ordered
   */
  MAX(0, "Max", "Max");

  /**
   * The '<em><b>Min</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Min</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #MIN
   * @model name="Min"
   * @generated
   * @ordered
   */
  public static final int MIN_VALUE = 0;

  /**
   * The '<em><b>Max</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Max</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #MAX
   * @model name="Max"
   * @generated
   * @ordered
   */
  public static final int MAX_VALUE = 0;

  /**
   * An array of all the '<em><b>Axis Origin</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final AxisOrigin[] VALUES_ARRAY =
    new AxisOrigin[]
    {
      MIN,
      MAX,
    };

  /**
   * A public read-only list of all the '<em><b>Axis Origin</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final List<AxisOrigin> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Axis Origin</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param literal the literal.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static AxisOrigin get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      AxisOrigin result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Axis Origin</b></em>' literal with the specified name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param name the name.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static AxisOrigin getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      AxisOrigin result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Axis Origin</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the integer value.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static AxisOrigin get(int value)
  {
    switch (value)
    {
      case MIN_VALUE: return MIN;
    }
    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private final int value;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private final String name;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private final String literal;

  /**
   * Only this class can construct instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private AxisOrigin(int value, String name, String literal)
  {
    this.value = value;
    this.name = name;
    this.literal = literal;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getLiteral()
  {
    return literal;
  }

  /**
   * Returns the literal value of the enumerator, which is its string representation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    return literal;
  }
  
} //AxisOrigin
