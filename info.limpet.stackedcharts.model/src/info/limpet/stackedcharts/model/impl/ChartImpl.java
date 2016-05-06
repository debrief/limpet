/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.Annotation;
import info.limpet.stackedcharts.model.Axis;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Chart</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link info.limpet.stackedcharts.model.impl.ChartImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.impl.ChartImpl#getAxes <em>Axes</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.impl.ChartImpl#getDatasets <em>Datasets</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.impl.ChartImpl#getName <em>Name</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.impl.ChartImpl#getAnnotations <em>Annotations</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ChartImpl extends MinimalEObjectImpl.Container implements Chart
{
  /**
   * The cached value of the '{@link #getAxes() <em>Axes</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAxes()
   * @generated
   * @ordered
   */
  protected EList<Axis> axes;

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
   * The cached value of the '{@link #getAnnotations() <em>Annotations</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAnnotations()
   * @generated
   * @ordered
   */
  protected EList<Annotation> annotations;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ChartImpl()
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
    return StackedchartsPackage.Literals.CHART;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ChartSet getParent()
  {
    if (eContainerFeatureID() != StackedchartsPackage.CHART__PARENT) return null;
    return (ChartSet)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetParent(ChartSet newParent, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newParent, StackedchartsPackage.CHART__PARENT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setParent(ChartSet newParent)
  {
    if (newParent != eInternalContainer() || (eContainerFeatureID() != StackedchartsPackage.CHART__PARENT && newParent != null))
    {
      if (EcoreUtil.isAncestor(this, newParent))
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      if (newParent != null)
        msgs = ((InternalEObject)newParent).eInverseAdd(this, StackedchartsPackage.CHART_SET__CHARTS, ChartSet.class, msgs);
      msgs = basicSetParent(newParent, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.CHART__PARENT, newParent, newParent));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Axis> getAxes()
  {
    if (axes == null)
    {
      axes = new EObjectContainmentWithInverseEList<Axis>(Axis.class, this, StackedchartsPackage.CHART__AXES, StackedchartsPackage.AXIS__PARENT);
    }
    return axes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Dataset> getDatasets()
  {
    if (datasets == null)
    {
      datasets = new EObjectContainmentEList<Dataset>(Dataset.class, this, StackedchartsPackage.CHART__DATASETS);
    }
    return datasets;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.CHART__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Annotation> getAnnotations()
  {
    if (annotations == null)
    {
      annotations = new EObjectContainmentWithInverseEList<Annotation>(Annotation.class, this, StackedchartsPackage.CHART__ANNOTATIONS, StackedchartsPackage.ANNOTATION__CHART);
    }
    return annotations;
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
      case StackedchartsPackage.CHART__PARENT:
        if (eInternalContainer() != null)
          msgs = eBasicRemoveFromContainer(msgs);
        return basicSetParent((ChartSet)otherEnd, msgs);
      case StackedchartsPackage.CHART__AXES:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getAxes()).basicAdd(otherEnd, msgs);
      case StackedchartsPackage.CHART__ANNOTATIONS:
        return ((InternalEList<InternalEObject>)(InternalEList<?>)getAnnotations()).basicAdd(otherEnd, msgs);
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
      case StackedchartsPackage.CHART__PARENT:
        return basicSetParent(null, msgs);
      case StackedchartsPackage.CHART__AXES:
        return ((InternalEList<?>)getAxes()).basicRemove(otherEnd, msgs);
      case StackedchartsPackage.CHART__DATASETS:
        return ((InternalEList<?>)getDatasets()).basicRemove(otherEnd, msgs);
      case StackedchartsPackage.CHART__ANNOTATIONS:
        return ((InternalEList<?>)getAnnotations()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
      case StackedchartsPackage.CHART__PARENT:
        return eInternalContainer().eInverseRemove(this, StackedchartsPackage.CHART_SET__CHARTS, ChartSet.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
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
      case StackedchartsPackage.CHART__PARENT:
        return getParent();
      case StackedchartsPackage.CHART__AXES:
        return getAxes();
      case StackedchartsPackage.CHART__DATASETS:
        return getDatasets();
      case StackedchartsPackage.CHART__NAME:
        return getName();
      case StackedchartsPackage.CHART__ANNOTATIONS:
        return getAnnotations();
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
      case StackedchartsPackage.CHART__PARENT:
        setParent((ChartSet)newValue);
        return;
      case StackedchartsPackage.CHART__AXES:
        getAxes().clear();
        getAxes().addAll((Collection<? extends Axis>)newValue);
        return;
      case StackedchartsPackage.CHART__DATASETS:
        getDatasets().clear();
        getDatasets().addAll((Collection<? extends Dataset>)newValue);
        return;
      case StackedchartsPackage.CHART__NAME:
        setName((String)newValue);
        return;
      case StackedchartsPackage.CHART__ANNOTATIONS:
        getAnnotations().clear();
        getAnnotations().addAll((Collection<? extends Annotation>)newValue);
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
      case StackedchartsPackage.CHART__PARENT:
        setParent((ChartSet)null);
        return;
      case StackedchartsPackage.CHART__AXES:
        getAxes().clear();
        return;
      case StackedchartsPackage.CHART__DATASETS:
        getDatasets().clear();
        return;
      case StackedchartsPackage.CHART__NAME:
        setName(NAME_EDEFAULT);
        return;
      case StackedchartsPackage.CHART__ANNOTATIONS:
        getAnnotations().clear();
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
      case StackedchartsPackage.CHART__PARENT:
        return getParent() != null;
      case StackedchartsPackage.CHART__AXES:
        return axes != null && !axes.isEmpty();
      case StackedchartsPackage.CHART__DATASETS:
        return datasets != null && !datasets.isEmpty();
      case StackedchartsPackage.CHART__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case StackedchartsPackage.CHART__ANNOTATIONS:
        return annotations != null && !annotations.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} //ChartImpl
