/**
 */
package info.limpet.stackedcharts.model.impl;

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
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;

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
 * </ul>
 *
 * @generated
 */
public class DependentAxisImpl extends AbstractAxisImpl implements DependentAxis {
	/**
	 * The cached value of the '{@link #getDatasets() <em>Datasets</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDatasets()
	 * @generated
	 * @ordered
	 */
	protected EList<Dataset> datasets;

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
				msgs = ((InternalEObject)newParent).eInverseAdd(this, StackedchartsPackage.CHART__MAX_AXES, Chart.class, msgs);
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
			datasets = new EObjectResolvingEList<Dataset>(Dataset.class, this, StackedchartsPackage.DEPENDENT_AXIS__DATASETS);
		}
		return datasets;
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
				return eInternalContainer().eInverseRemove(this, StackedchartsPackage.CHART__MAX_AXES, Chart.class, msgs);
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
		}
		return super.eIsSet(featureID);
	}

} //DependentAxisImpl
