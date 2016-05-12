/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.StackedchartsPackage;

import java.util.Collection;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Dependent Axis</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
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
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
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
			case StackedchartsPackage.DEPENDENT_AXIS__DATASETS:
				return datasets != null && !datasets.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //DependentAxisImpl
