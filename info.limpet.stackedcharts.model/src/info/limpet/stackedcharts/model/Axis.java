/**
 */
package info.limpet.stackedcharts.model;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Axis</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link info.limpet.stackedcharts.model.Axis#getParent <em>Parent</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.Axis#getName <em>Name</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.Axis#getOrigin <em>Origin</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.Axis#getScale <em>Scale</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAxis()
 * @model
 * @generated
 */
public interface Axis extends EObject
{
  /**
   * Returns the value of the '<em><b>Parent</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link info.limpet.stackedcharts.model.Chart#getAxes <em>Axes</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parent</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Parent</em>' container reference.
   * @see #setParent(Chart)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAxis_Parent()
   * @see info.limpet.stackedcharts.model.Chart#getAxes
   * @model opposite="axes" transient="false"
   * @generated
   */
  Chart getParent();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Axis#getParent <em>Parent</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Parent</em>' container reference.
   * @see #getParent()
   * @generated
   */
  void setParent(Chart value);

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAxis_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Axis#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Origin</b></em>' attribute.
   * The literals are from the enumeration {@link info.limpet.stackedcharts.model.AxisOrigin}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Origin</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Origin</em>' attribute.
   * @see info.limpet.stackedcharts.model.AxisOrigin
   * @see #setOrigin(AxisOrigin)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAxis_Origin()
   * @model
   * @generated
   */
  AxisOrigin getOrigin();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Axis#getOrigin <em>Origin</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Origin</em>' attribute.
   * @see info.limpet.stackedcharts.model.AxisOrigin
   * @see #getOrigin()
   * @generated
   */
  void setOrigin(AxisOrigin value);

  /**
   * Returns the value of the '<em><b>Scale</b></em>' attribute.
   * The literals are from the enumeration {@link info.limpet.stackedcharts.model.AxisScale}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Scale</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Scale</em>' attribute.
   * @see info.limpet.stackedcharts.model.AxisScale
   * @see #setScale(AxisScale)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAxis_Scale()
   * @model
   * @generated
   */
  AxisScale getScale();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Axis#getScale <em>Scale</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Scale</em>' attribute.
   * @see info.limpet.stackedcharts.model.AxisScale
   * @see #getScale()
   * @generated
   */
  void setScale(AxisScale value);

} // Axis
