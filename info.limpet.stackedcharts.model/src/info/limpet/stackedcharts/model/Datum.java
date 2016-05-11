/**
 */
package info.limpet.stackedcharts.model;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Datum</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link info.limpet.stackedcharts.model.Datum#getIndependentVal <em>Independent Val</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDatum()
 * @model
 * @generated
 */
public interface Datum extends EObject {
	/**
	 * Returns the value of the '<em><b>Independent Val</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Independent Val</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Independent Val</em>' attribute.
	 * @see #setIndependentVal(double)
	 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDatum_IndependentVal()
	 * @model
	 * @generated
	 */
	double getIndependentVal();

	/**
	 * Sets the value of the '{@link info.limpet.stackedcharts.model.Datum#getIndependentVal <em>Independent Val</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Independent Val</em>' attribute.
	 * @see #getIndependentVal()
	 * @generated
	 */
	void setIndependentVal(double value);

} // Datum
