/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.StackedchartsPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Datum</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link info.limpet.stackedcharts.model.impl.DatumImpl#getIndependentVal <em>Independent Val</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DatumImpl extends MinimalEObjectImpl.Container implements Datum {
	/**
	 * The default value of the '{@link #getIndependentVal() <em>Independent Val</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIndependentVal()
	 * @generated
	 * @ordered
	 */
	protected static final double INDEPENDENT_VAL_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getIndependentVal() <em>Independent Val</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIndependentVal()
	 * @generated
	 * @ordered
	 */
	protected double independentVal = INDEPENDENT_VAL_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DatumImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return StackedchartsPackage.Literals.DATUM;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public double getIndependentVal() {
		return independentVal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIndependentVal(double newIndependentVal) {
		double oldIndependentVal = independentVal;
		independentVal = newIndependentVal;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.DATUM__INDEPENDENT_VAL, oldIndependentVal, independentVal));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case StackedchartsPackage.DATUM__INDEPENDENT_VAL:
				return getIndependentVal();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case StackedchartsPackage.DATUM__INDEPENDENT_VAL:
				setIndependentVal((Double)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case StackedchartsPackage.DATUM__INDEPENDENT_VAL:
				setIndependentVal(INDEPENDENT_VAL_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case StackedchartsPackage.DATUM__INDEPENDENT_VAL:
				return independentVal != INDEPENDENT_VAL_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (independentVal: ");
		result.append(independentVal);
		result.append(')');
		return result.toString();
	}

} //DatumImpl
