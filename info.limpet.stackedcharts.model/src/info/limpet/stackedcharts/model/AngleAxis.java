/**
 */
package info.limpet.stackedcharts.model;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Angle Axis</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Axis that wraps around at a particular value -  used with cyclic dimensions such as angles
 * 
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link info.limpet.stackedcharts.model.AngleAxis#getMinVal <em>Min Val</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.AngleAxis#getMaxVal <em>Max Val</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAngleAxis()
 * @model
 * @generated
 */
public interface AngleAxis extends NumberAxis
{

  /**
   * Returns the value of the '<em><b>Min Val</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Min Val</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Min Val</em>' attribute.
   * @see #setMinVal(double)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAngleAxis_MinVal()
   * @model dataType="org.eclipse.emf.ecore.xml.type.Double"
   * @generated
   */
  double getMinVal();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.AngleAxis#getMinVal <em>Min Val</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Min Val</em>' attribute.
   * @see #getMinVal()
   * @generated
   */
  void setMinVal(double value);

  /**
   * Returns the value of the '<em><b>Max Val</b></em>' attribute.
   * The default value is <code>"0.0"</code>.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Max Val</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Max Val</em>' attribute.
   * @see #setMaxVal(double)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAngleAxis_MaxVal()
   * @model default="0.0" dataType="org.eclipse.emf.ecore.xml.type.Double"
   * @generated
   */
  double getMaxVal();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.AngleAxis#getMaxVal <em>Max Val</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Max Val</em>' attribute.
   * @see #getMaxVal()
   * @generated
   */
  void setMaxVal(double value);
} // AngleAxis
