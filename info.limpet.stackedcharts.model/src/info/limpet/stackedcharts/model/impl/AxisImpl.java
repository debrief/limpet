/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.Axis;
import info.limpet.stackedcharts.model.AxisOrigin;
import info.limpet.stackedcharts.model.AxisScale;
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
 * An implementation of the model object '<em><b>Axis</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link info.limpet.stackedcharts.model.impl.AxisImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.impl.AxisImpl#getName <em>Name</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.impl.AxisImpl#getOrigin <em>Origin</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.impl.AxisImpl#getScale <em>Scale</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AxisImpl extends MinimalEObjectImpl.Container implements Axis
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
   * The default value of the '{@link #getOrigin() <em>Origin</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOrigin()
   * @generated
   * @ordered
   */
  protected static final AxisOrigin ORIGIN_EDEFAULT = AxisOrigin.MIN;

  /**
   * The cached value of the '{@link #getOrigin() <em>Origin</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOrigin()
   * @generated
   * @ordered
   */
  protected AxisOrigin origin = ORIGIN_EDEFAULT;

  /**
   * The default value of the '{@link #getScale() <em>Scale</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getScale()
   * @generated
   * @ordered
   */
  protected static final AxisScale SCALE_EDEFAULT = AxisScale.LINEAR;

  /**
   * The cached value of the '{@link #getScale() <em>Scale</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getScale()
   * @generated
   * @ordered
   */
  protected AxisScale scale = SCALE_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AxisImpl()
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
    return StackedchartsPackage.Literals.AXIS;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Chart getParent()
  {
    if (eContainerFeatureID() != StackedchartsPackage.AXIS__PARENT) return null;
    return (Chart)eInternalContainer();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetParent(Chart newParent, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newParent, StackedchartsPackage.AXIS__PARENT, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setParent(Chart newParent)
  {
    if (newParent != eInternalContainer() || (eContainerFeatureID() != StackedchartsPackage.AXIS__PARENT && newParent != null))
    {
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
      eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.AXIS__PARENT, newParent, newParent));
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
      eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.AXIS__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AxisOrigin getOrigin()
  {
    return origin;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setOrigin(AxisOrigin newOrigin)
  {
    AxisOrigin oldOrigin = origin;
    origin = newOrigin == null ? ORIGIN_EDEFAULT : newOrigin;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.AXIS__ORIGIN, oldOrigin, origin));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AxisScale getScale()
  {
    return scale;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setScale(AxisScale newScale)
  {
    AxisScale oldScale = scale;
    scale = newScale == null ? SCALE_EDEFAULT : newScale;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.AXIS__SCALE, oldScale, scale));
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
      case StackedchartsPackage.AXIS__PARENT:
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
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case StackedchartsPackage.AXIS__PARENT:
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
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
      case StackedchartsPackage.AXIS__PARENT:
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
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case StackedchartsPackage.AXIS__PARENT:
        return getParent();
      case StackedchartsPackage.AXIS__NAME:
        return getName();
      case StackedchartsPackage.AXIS__ORIGIN:
        return getOrigin();
      case StackedchartsPackage.AXIS__SCALE:
        return getScale();
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
      case StackedchartsPackage.AXIS__PARENT:
        setParent((Chart)newValue);
        return;
      case StackedchartsPackage.AXIS__NAME:
        setName((String)newValue);
        return;
      case StackedchartsPackage.AXIS__ORIGIN:
        setOrigin((AxisOrigin)newValue);
        return;
      case StackedchartsPackage.AXIS__SCALE:
        setScale((AxisScale)newValue);
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
      case StackedchartsPackage.AXIS__PARENT:
        setParent((Chart)null);
        return;
      case StackedchartsPackage.AXIS__NAME:
        setName(NAME_EDEFAULT);
        return;
      case StackedchartsPackage.AXIS__ORIGIN:
        setOrigin(ORIGIN_EDEFAULT);
        return;
      case StackedchartsPackage.AXIS__SCALE:
        setScale(SCALE_EDEFAULT);
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
      case StackedchartsPackage.AXIS__PARENT:
        return getParent() != null;
      case StackedchartsPackage.AXIS__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case StackedchartsPackage.AXIS__ORIGIN:
        return origin != ORIGIN_EDEFAULT;
      case StackedchartsPackage.AXIS__SCALE:
        return scale != SCALE_EDEFAULT;
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
    result.append(", origin: ");
    result.append(origin);
    result.append(", scale: ");
    result.append(scale);
    result.append(')');
    return result.toString();
  }

} //AxisImpl
