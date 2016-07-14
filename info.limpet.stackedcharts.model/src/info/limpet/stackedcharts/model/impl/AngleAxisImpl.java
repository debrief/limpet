/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.AngleAxis;
import info.limpet.stackedcharts.model.StackedchartsPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Angle Axis</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link info.limpet.stackedcharts.model.impl.AngleAxisImpl#getMinVal <em>Min Val</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.impl.AngleAxisImpl#getMaxVal <em>Max Val</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AngleAxisImpl extends NumberAxisImpl implements AngleAxis
{
  /**
   * The default value of the '{@link #getMinVal() <em>Min Val</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMinVal()
   * @generated
   * @ordered
   */
  protected static final double MIN_VAL_EDEFAULT = 0.0;
  /**
   * The cached value of the '{@link #getMinVal() <em>Min Val</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMinVal()
   * @generated
   * @ordered
   */
  protected double minVal = MIN_VAL_EDEFAULT;
  /**
   * The default value of the '{@link #getMaxVal() <em>Max Val</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMaxVal()
   * @generated
   * @ordered
   */
  protected static final double MAX_VAL_EDEFAULT = 0.0;
  /**
   * The cached value of the '{@link #getMaxVal() <em>Max Val</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMaxVal()
   * @generated
   * @ordered
   */
  protected double maxVal = MAX_VAL_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AngleAxisImpl()
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
    return StackedchartsPackage.Literals.ANGLE_AXIS;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public double getMinVal()
  {
    return minVal;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setMinVal(double newMinVal)
  {
    double oldMinVal = minVal;
    minVal = newMinVal;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ANGLE_AXIS__MIN_VAL, oldMinVal, minVal));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public double getMaxVal()
  {
    return maxVal;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setMaxVal(double newMaxVal)
  {
    double oldMaxVal = maxVal;
    maxVal = newMaxVal;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ANGLE_AXIS__MAX_VAL, oldMaxVal, maxVal));
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
      case StackedchartsPackage.ANGLE_AXIS__MIN_VAL:
        return getMinVal();
      case StackedchartsPackage.ANGLE_AXIS__MAX_VAL:
        return getMaxVal();
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
      case StackedchartsPackage.ANGLE_AXIS__MIN_VAL:
        setMinVal((Double)newValue);
        return;
      case StackedchartsPackage.ANGLE_AXIS__MAX_VAL:
        setMaxVal((Double)newValue);
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
      case StackedchartsPackage.ANGLE_AXIS__MIN_VAL:
        setMinVal(MIN_VAL_EDEFAULT);
        return;
      case StackedchartsPackage.ANGLE_AXIS__MAX_VAL:
        setMaxVal(MAX_VAL_EDEFAULT);
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
      case StackedchartsPackage.ANGLE_AXIS__MIN_VAL:
        return minVal != MIN_VAL_EDEFAULT;
      case StackedchartsPackage.ANGLE_AXIS__MAX_VAL:
        return maxVal != MAX_VAL_EDEFAULT;
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
    result.append(" (minVal: ");
    result.append(minVal);
    result.append(", maxVal: ");
    result.append(maxVal);
    result.append(')');
    return result.toString();
  }

} //AngleAxisImpl
