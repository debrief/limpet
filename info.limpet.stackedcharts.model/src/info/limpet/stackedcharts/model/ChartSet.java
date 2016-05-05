/**
 */
package info.limpet.stackedcharts.model;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Chart Set</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link info.limpet.stackedcharts.model.ChartSet#getCharts <em>Charts</em>}</li>
 * </ul>
 * </p>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getChartSet()
 * @model
 * @generated
 */
public interface ChartSet extends EObject
{
  /**
   * Returns the value of the '<em><b>Charts</b></em>' containment reference list.
   * The list contents are of type {@link info.limpet.stackedcharts.model.Chart}.
   * It is bidirectional and its opposite is '{@link info.limpet.stackedcharts.model.Chart#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Charts</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Charts</em>' containment reference list.
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getChartSet_Charts()
   * @see info.limpet.stackedcharts.model.Chart#getParent
   * @model opposite="parent" containment="true"
   * @generated
   */
  EList<Chart> getCharts();

} // ChartSet
