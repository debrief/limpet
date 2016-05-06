/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.Axis;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.model.Styling;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Dataset</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link info.limpet.stackedcharts.model.impl.DatasetImpl#getName <em>Name</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.impl.DatasetImpl#getAxis <em>Axis</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.impl.DatasetImpl#getItems <em>Items</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.impl.DatasetImpl#getStyling <em>Styling</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DatasetImpl extends MinimalEObjectImpl.Container implements Dataset
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
   * The cached value of the '{@link #getAxis() <em>Axis</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAxis()
   * @generated
   * @ordered
   */
  protected Axis axis;

  /**
   * The cached value of the '{@link #getItems() <em>Items</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getItems()
   * @generated
   * @ordered
   */
  protected EList<DataItem> items;

  /**
   * The cached value of the '{@link #getStyling() <em>Styling</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStyling()
   * @generated
   * @ordered
   */
  protected Styling styling;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DatasetImpl()
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
    return StackedchartsPackage.Literals.DATASET;
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
      eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.DATASET__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Axis getAxis()
  {
    if (axis != null && axis.eIsProxy())
    {
      InternalEObject oldAxis = (InternalEObject)axis;
      axis = (Axis)eResolveProxy(oldAxis);
      if (axis != oldAxis)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, StackedchartsPackage.DATASET__AXIS, oldAxis, axis));
      }
    }
    return axis;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Axis basicGetAxis()
  {
    return axis;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAxis(Axis newAxis)
  {
    Axis oldAxis = axis;
    axis = newAxis;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.DATASET__AXIS, oldAxis, axis));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<DataItem> getItems()
  {
    if (items == null)
    {
      items = new EObjectContainmentEList<DataItem>(DataItem.class, this, StackedchartsPackage.DATASET__ITEMS);
    }
    return items;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Styling getStyling()
  {
    return styling;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetStyling(Styling newStyling, NotificationChain msgs)
  {
    Styling oldStyling = styling;
    styling = newStyling;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, StackedchartsPackage.DATASET__STYLING, oldStyling, newStyling);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setStyling(Styling newStyling)
  {
    if (newStyling != styling)
    {
      NotificationChain msgs = null;
      if (styling != null)
        msgs = ((InternalEObject)styling).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - StackedchartsPackage.DATASET__STYLING, null, msgs);
      if (newStyling != null)
        msgs = ((InternalEObject)newStyling).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - StackedchartsPackage.DATASET__STYLING, null, msgs);
      msgs = basicSetStyling(newStyling, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.DATASET__STYLING, newStyling, newStyling));
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
      case StackedchartsPackage.DATASET__ITEMS:
        return ((InternalEList<?>)getItems()).basicRemove(otherEnd, msgs);
      case StackedchartsPackage.DATASET__STYLING:
        return basicSetStyling(null, msgs);
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
      case StackedchartsPackage.DATASET__NAME:
        return getName();
      case StackedchartsPackage.DATASET__AXIS:
        if (resolve) return getAxis();
        return basicGetAxis();
      case StackedchartsPackage.DATASET__ITEMS:
        return getItems();
      case StackedchartsPackage.DATASET__STYLING:
        return getStyling();
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
      case StackedchartsPackage.DATASET__NAME:
        setName((String)newValue);
        return;
      case StackedchartsPackage.DATASET__AXIS:
        setAxis((Axis)newValue);
        return;
      case StackedchartsPackage.DATASET__ITEMS:
        getItems().clear();
        getItems().addAll((Collection<? extends DataItem>)newValue);
        return;
      case StackedchartsPackage.DATASET__STYLING:
        setStyling((Styling)newValue);
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
      case StackedchartsPackage.DATASET__NAME:
        setName(NAME_EDEFAULT);
        return;
      case StackedchartsPackage.DATASET__AXIS:
        setAxis((Axis)null);
        return;
      case StackedchartsPackage.DATASET__ITEMS:
        getItems().clear();
        return;
      case StackedchartsPackage.DATASET__STYLING:
        setStyling((Styling)null);
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
      case StackedchartsPackage.DATASET__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case StackedchartsPackage.DATASET__AXIS:
        return axis != null;
      case StackedchartsPackage.DATASET__ITEMS:
        return items != null && !items.isEmpty();
      case StackedchartsPackage.DATASET__STYLING:
        return styling != null;
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

} //DatasetImpl
