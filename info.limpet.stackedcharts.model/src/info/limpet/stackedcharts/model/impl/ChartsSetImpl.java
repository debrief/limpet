/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartsSet;
import info.limpet.stackedcharts.model.StackedchartsPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Charts Set</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link info.limpet.stackedcharts.model.impl.ChartsSetImpl#getCharts <em>Charts</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ChartsSetImpl extends MinimalEObjectImpl.Container implements ChartsSet
{
  /**
   * The cached value of the '{@link #getCharts() <em>Charts</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCharts()
   * @generated
   * @ordered
   */
  protected EList<Chart> charts;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ChartsSetImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return StackedchartsPackage.Literals.CHARTS_SET;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Chart> getCharts()
  {
    if (charts == null)
    {
      charts = new EObjectContainmentWithInverseEList<Chart>(Chart.class, this, StackedchartsPackage.CHARTS_SET__CHARTS, StackedchartsPackage.CHART__PARENT);
    }
    return charts;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case StackedchartsPackage.CHARTS_SET__CHARTS:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getCharts()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case StackedchartsPackage.CHARTS_SET__CHARTS:
        return ((InternalEList<?>)getCharts()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case StackedchartsPackage.CHARTS_SET__CHARTS:
        return getCharts();
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
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case StackedchartsPackage.CHARTS_SET__CHARTS:
        getCharts().clear();
        getCharts().addAll((Collection<? extends Chart>)newValue);
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
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case StackedchartsPackage.CHARTS_SET__CHARTS:
        getCharts().clear();
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
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case StackedchartsPackage.CHARTS_SET__CHARTS:
        return charts != null && !charts.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //ChartsSetImpl
