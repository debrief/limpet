/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.AxisOrigin;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.StackedchartsPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Dependent Axis</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link info.limpet.stackedcharts.model.impl.DependentAxisImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.impl.DependentAxisImpl#getDatasets <em>Datasets</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.impl.DependentAxisImpl#getAxisOrigin <em>Axis Origin</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DependentAxisImpl extends AbstractAxisImpl implements DependentAxis {
	/**
	 * The cached value of the '{@link #getDatasets() <em>Datasets</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDatasets()
	 * @generated
	 * @ordered
	 */
	protected EList<Dataset> datasets;

	/**
	 * The default value of the '{@link #getAxisOrigin() <em>Axis Origin</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAxisOrigin()
	 * @generated
	 * @ordered
	 */
	protected static final AxisOrigin AXIS_ORIGIN_EDEFAULT = AxisOrigin.MIN;

	/**
	 * The cached value of the '{@link #getAxisOrigin() <em>Axis Origin</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAxisOrigin()
	 * @generated
	 * @ordered
	 */
	protected AxisOrigin axisOrigin = AXIS_ORIGIN_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DependentAxisImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return StackedchartsPackage.Literals.DEPENDENT_AXIS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Chart getParent() {
		if (eContainerFeatureID() != StackedchartsPackage.DEPENDENT_AXIS__PARENT) return null;
		return (Chart)eInternalContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParent(Chart newParent, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newParent, StackedchartsPackage.DEPENDENT_AXIS__PARENT, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParent(Chart newParent) {
		if (newParent != eInternalContainer() || (eContainerFeatureID() != StackedchartsPackage.DEPENDENT_AXIS__PARENT && newParent != null)) {
			if (EcoreUtil.isAncestor(this, newParent))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newParent != null)
				msgs = ((InternalEObject)newParent).eInverseAdd(this, StackedchartsPackage.CHART__AXES, Chart.class, msgs);
			msgs = basicSetParent(newParent, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.DEPENDENT_AXIS__PARENT, newParent, newParent));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Dataset> getDatasets() {
		if (datasets == null) {
			datasets = new EObjectContainmentEList<Dataset>(Dataset.class, this, StackedchartsPackage.DEPENDENT_AXIS__DATASETS);
		}
		return datasets;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AxisOrigin getAxisOrigin() {
		return axisOrigin;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAxisOrigin(AxisOrigin newAxisOrigin) {
		AxisOrigin oldAxisOrigin = axisOrigin;
		axisOrigin = newAxisOrigin == null ? AXIS_ORIGIN_EDEFAULT : newAxisOrigin;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.DEPENDENT_AXIS__AXIS_ORIGIN, oldAxisOrigin, axisOrigin));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case StackedchartsPackage.DEPENDENT_AXIS__PARENT:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetParent((Chart)otherEnd, msgs);
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
			case StackedchartsPackage.DEPENDENT_AXIS__PARENT:
				return basicSetParent(null, msgs);
			case StackedchartsPackage.DEPENDENT_AXIS__DATASETS:
				return ((InternalEList<?>)getDatasets()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
			case StackedchartsPackage.DEPENDENT_AXIS__PARENT:
				return eInternalContainer().eInverseRemove(this, StackedchartsPackage.CHART__AXES, Chart.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case StackedchartsPackage.DEPENDENT_AXIS__PARENT:
				return getParent();
			case StackedchartsPackage.DEPENDENT_AXIS__DATASETS:
				return getDatasets();
			case StackedchartsPackage.DEPENDENT_AXIS__AXIS_ORIGIN:
				return getAxisOrigin();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case StackedchartsPackage.DEPENDENT_AXIS__PARENT:
				setParent((Chart)newValue);
				return;
			case StackedchartsPackage.DEPENDENT_AXIS__DATASETS:
				getDatasets().clear();
				getDatasets().addAll((Collection<? extends Dataset>)newValue);
				return;
			case StackedchartsPackage.DEPENDENT_AXIS__AXIS_ORIGIN:
				setAxisOrigin((AxisOrigin)newValue);
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
			case StackedchartsPackage.DEPENDENT_AXIS__PARENT:
				setParent((Chart)null);
				return;
			case StackedchartsPackage.DEPENDENT_AXIS__DATASETS:
				getDatasets().clear();
				return;
			case StackedchartsPackage.DEPENDENT_AXIS__AXIS_ORIGIN:
				setAxisOrigin(AXIS_ORIGIN_EDEFAULT);
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
			case StackedchartsPackage.DEPENDENT_AXIS__PARENT:
				return getParent() != null;
			case StackedchartsPackage.DEPENDENT_AXIS__DATASETS:
				return datasets != null && !datasets.isEmpty();
			case StackedchartsPackage.DEPENDENT_AXIS__AXIS_ORIGIN:
				return axisOrigin != AXIS_ORIGIN_EDEFAULT;
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
		result.append(" (axisOrigin: ");
		result.append(axisOrigin);
		result.append(')');
		return result.toString();
	}

} //DependentAxisImpl
