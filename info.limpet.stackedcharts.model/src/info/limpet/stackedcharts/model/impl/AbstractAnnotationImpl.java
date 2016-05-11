/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.AbstractAnnotation;
import info.limpet.stackedcharts.model.AbstractAxis;
import info.limpet.stackedcharts.model.StackedchartsPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Abstract Annotation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link info.limpet.stackedcharts.model.impl.AbstractAnnotationImpl#getName <em>Name</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.impl.AbstractAnnotationImpl#getColor <em>Color</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.impl.AbstractAnnotationImpl#getChart <em>Chart</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class AbstractAnnotationImpl extends MinimalEObjectImpl.Container implements AbstractAnnotation {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getColor() <em>Color</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColor()
	 * @generated
	 * @ordered
	 */
	protected static final String COLOR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getColor() <em>Color</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColor()
	 * @generated
	 * @ordered
	 */
	protected String color = COLOR_EDEFAULT;

	/**
	 * The cached value of the '{@link #getChart() <em>Chart</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getChart()
	 * @generated
	 * @ordered
	 */
	protected AbstractAxis chart;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AbstractAnnotationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return StackedchartsPackage.Literals.ABSTRACT_ANNOTATION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ABSTRACT_ANNOTATION__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getColor() {
		return color;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setColor(String newColor) {
		String oldColor = color;
		color = newColor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ABSTRACT_ANNOTATION__COLOR, oldColor, color));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AbstractAxis getChart() {
		if (chart != null && chart.eIsProxy()) {
			InternalEObject oldChart = (InternalEObject)chart;
			chart = (AbstractAxis)eResolveProxy(oldChart);
			if (chart != oldChart) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, StackedchartsPackage.ABSTRACT_ANNOTATION__CHART, oldChart, chart));
			}
		}
		return chart;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AbstractAxis basicGetChart() {
		return chart;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetChart(AbstractAxis newChart, NotificationChain msgs) {
		AbstractAxis oldChart = chart;
		chart = newChart;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ABSTRACT_ANNOTATION__CHART, oldChart, newChart);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setChart(AbstractAxis newChart) {
		if (newChart != chart) {
			NotificationChain msgs = null;
			if (chart != null)
				msgs = ((InternalEObject)chart).eInverseRemove(this, StackedchartsPackage.ABSTRACT_AXIS__ANNOTATIONS, AbstractAxis.class, msgs);
			if (newChart != null)
				msgs = ((InternalEObject)newChart).eInverseAdd(this, StackedchartsPackage.ABSTRACT_AXIS__ANNOTATIONS, AbstractAxis.class, msgs);
			msgs = basicSetChart(newChart, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ABSTRACT_ANNOTATION__CHART, newChart, newChart));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case StackedchartsPackage.ABSTRACT_ANNOTATION__CHART:
				if (chart != null)
					msgs = ((InternalEObject)chart).eInverseRemove(this, StackedchartsPackage.ABSTRACT_AXIS__ANNOTATIONS, AbstractAxis.class, msgs);
				return basicSetChart((AbstractAxis)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case StackedchartsPackage.ABSTRACT_ANNOTATION__CHART:
				return basicSetChart(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case StackedchartsPackage.ABSTRACT_ANNOTATION__NAME:
				return getName();
			case StackedchartsPackage.ABSTRACT_ANNOTATION__COLOR:
				return getColor();
			case StackedchartsPackage.ABSTRACT_ANNOTATION__CHART:
				if (resolve) return getChart();
				return basicGetChart();
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
			case StackedchartsPackage.ABSTRACT_ANNOTATION__NAME:
				setName((String)newValue);
				return;
			case StackedchartsPackage.ABSTRACT_ANNOTATION__COLOR:
				setColor((String)newValue);
				return;
			case StackedchartsPackage.ABSTRACT_ANNOTATION__CHART:
				setChart((AbstractAxis)newValue);
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
			case StackedchartsPackage.ABSTRACT_ANNOTATION__NAME:
				setName(NAME_EDEFAULT);
				return;
			case StackedchartsPackage.ABSTRACT_ANNOTATION__COLOR:
				setColor(COLOR_EDEFAULT);
				return;
			case StackedchartsPackage.ABSTRACT_ANNOTATION__CHART:
				setChart((AbstractAxis)null);
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
			case StackedchartsPackage.ABSTRACT_ANNOTATION__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case StackedchartsPackage.ABSTRACT_ANNOTATION__COLOR:
				return COLOR_EDEFAULT == null ? color != null : !COLOR_EDEFAULT.equals(color);
			case StackedchartsPackage.ABSTRACT_ANNOTATION__CHART:
				return chart != null;
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
		result.append(" (name: ");
		result.append(name);
		result.append(", color: ");
		result.append(color);
		result.append(')');
		return result.toString();
	}

} //AbstractAnnotationImpl
