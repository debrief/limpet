/**
 */
package info.limpet.stackedcharts.model;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Dependent Axis</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link info.limpet.stackedcharts.model.DependentAxis#getParent <em>Parent</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.DependentAxis#getDatasets <em>Datasets</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.DependentAxis#getAxisOrigin <em>Axis Origin</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDependentAxis()
 * @model
 * @generated
 */
public interface DependentAxis extends AbstractAxis {
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
	 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDependentAxis_Parent()
	 * @see info.limpet.stackedcharts.model.Chart#getAxes
	 * @model opposite="axes" transient="false"
	 * @generated
	 */
	Chart getParent();

	/**
	 * Sets the value of the '{@link info.limpet.stackedcharts.model.DependentAxis#getParent <em>Parent</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent</em>' container reference.
	 * @see #getParent()
	 * @generated
	 */
	void setParent(Chart value);

	/**
	 * Returns the value of the '<em><b>Datasets</b></em>' reference list.
	 * The list contents are of type {@link info.limpet.stackedcharts.model.Dataset}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Datasets</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Datasets</em>' reference list.
	 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDependentAxis_Datasets()
	 * @model
	 * @generated
	 */
	EList<Dataset> getDatasets();

	/**
	 * Returns the value of the '<em><b>Axis Origin</b></em>' attribute.
	 * The literals are from the enumeration {@link info.limpet.stackedcharts.model.AxisOrigin}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Axis Origin</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Axis Origin</em>' attribute.
	 * @see info.limpet.stackedcharts.model.AxisOrigin
	 * @see #setAxisOrigin(AxisOrigin)
	 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDependentAxis_AxisOrigin()
	 * @model
	 * @generated
	 */
	AxisOrigin getAxisOrigin();

	/**
	 * Sets the value of the '{@link info.limpet.stackedcharts.model.DependentAxis#getAxisOrigin <em>Axis Origin</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Axis Origin</em>' attribute.
	 * @see info.limpet.stackedcharts.model.AxisOrigin
	 * @see #getAxisOrigin()
	 * @generated
	 */
	void setAxisOrigin(AxisOrigin value);

} // DependentAxis
