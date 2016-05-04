/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.Annotation;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.StackedchartsPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Annotation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link info.limpet.stackedcharts.model.impl.AnnotationImpl#getName <em>Name</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.impl.AnnotationImpl#getColor <em>Color</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.impl.AnnotationImpl#getChart <em>Chart</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AnnotationImpl extends MinimalEObjectImpl.Container implements Annotation
{
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
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AnnotationImpl()
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
    return StackedchartsPackage.Literals.ANNOTATION;
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
      eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ANNOTATION__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getColor()
  {
    return color;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setColor(String newColor)
  {
    String oldColor = color;
    color = newColor;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ANNOTATION__COLOR, oldColor, color));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Chart getChart()
  {
    if (eContainerFeatureID() != StackedchartsPackage.ANNOTATION__CHART) return null;
    return (Chart)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetChart(Chart newChart, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newChart, StackedchartsPackage.ANNOTATION__CHART, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setChart(Chart newChart)
  {
    if (newChart != eInternalContainer() || (eContainerFeatureID() != StackedchartsPackage.ANNOTATION__CHART && newChart != null))
    {
      if (EcoreUtil.isAncestor(this, newChart))
        throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
      NotificationChain msgs = null;
      if (eInternalContainer() != null)
        msgs = eBasicRemoveFromContainer(msgs);
      if (newChart != null)
        msgs = ((InternalEObject)newChart).eInverseAdd(this, StackedchartsPackage.CHART__ANNOTATIONS, Chart.class, msgs);
      msgs = basicSetChart(newChart, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ANNOTATION__CHART, newChart, newChart));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case StackedchartsPackage.ANNOTATION__CHART:
        if (eInternalContainer() != null)
          msgs = eBasicRemoveFromContainer(msgs);
        return basicSetChart((Chart)otherEnd, msgs);
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
      case StackedchartsPackage.ANNOTATION__CHART:
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
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
      case StackedchartsPackage.ANNOTATION__CHART:
        return eInternalContainer().eInverseRemove(this, StackedchartsPackage.CHART__ANNOTATIONS, Chart.class, msgs);
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
      case StackedchartsPackage.ANNOTATION__NAME:
        return getName();
      case StackedchartsPackage.ANNOTATION__COLOR:
        return getColor();
      case StackedchartsPackage.ANNOTATION__CHART:
        return getChart();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case StackedchartsPackage.ANNOTATION__NAME:
        setName((String)newValue);
        return;
      case StackedchartsPackage.ANNOTATION__COLOR:
        setColor((String)newValue);
        return;
      case StackedchartsPackage.ANNOTATION__CHART:
        setChart((Chart)newValue);
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
      case StackedchartsPackage.ANNOTATION__NAME:
        setName(NAME_EDEFAULT);
        return;
      case StackedchartsPackage.ANNOTATION__COLOR:
        setColor(COLOR_EDEFAULT);
        return;
      case StackedchartsPackage.ANNOTATION__CHART:
        setChart((Chart)null);
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
      case StackedchartsPackage.ANNOTATION__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case StackedchartsPackage.ANNOTATION__COLOR:
        return COLOR_EDEFAULT == null ? color != null : !COLOR_EDEFAULT.equals(color);
      case StackedchartsPackage.ANNOTATION__CHART:
        return getChart() != null;
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
    result.append(", color: ");
    result.append(color);
    result.append(')');
    return result.toString();
  }

} //AnnotationImpl
