/**
 */
package info.limpet.stackedcharts.model;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Annotation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link info.limpet.stackedcharts.model.Annotation#getName <em>Name</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.Annotation#getColor <em>Color</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.Annotation#getChart <em>Chart</em>}</li>
 * </ul>
 * </p>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAnnotation()
 * @model
 * @generated
 */
public interface Annotation extends EObject
{
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
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAnnotation_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Annotation#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Color</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Color</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Color</em>' attribute.
   * @see #setColor(String)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAnnotation_Color()
   * @model
   * @generated
   */
  String getColor();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Annotation#getColor <em>Color</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Color</em>' attribute.
   * @see #getColor()
   * @generated
   */
  void setColor(String value);

  /**
   * Returns the value of the '<em><b>Chart</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link info.limpet.stackedcharts.model.Chart#getAnnotations <em>Annotations</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Chart</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Chart</em>' container reference.
   * @see #setChart(Chart)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAnnotation_Chart()
   * @see info.limpet.stackedcharts.model.Chart#getAnnotations
   * @model opposite="annotations" transient="false"
   * @generated
   */
  Chart getChart();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Annotation#getChart <em>Chart</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Chart</em>' container reference.
   * @see #getChart()
   * @generated
   */
  void setChart(Chart value);

} // Annotation
