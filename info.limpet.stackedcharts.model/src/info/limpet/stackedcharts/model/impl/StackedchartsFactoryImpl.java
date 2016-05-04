/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class StackedchartsFactoryImpl extends EFactoryImpl implements StackedchartsFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static StackedchartsFactory init()
  {
    try
    {
      StackedchartsFactory theStackedchartsFactory = (StackedchartsFactory)EPackage.Registry.INSTANCE.getEFactory(StackedchartsPackage.eNS_URI);
      if (theStackedchartsFactory != null)
      {
        return theStackedchartsFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new StackedchartsFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StackedchartsFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case StackedchartsPackage.CHARTS_SET: return createChartsSet();
      case StackedchartsPackage.CHART: return createChart();
      case StackedchartsPackage.AXIS: return createAxis();
      case StackedchartsPackage.DATASET: return createDataset();
      case StackedchartsPackage.DATA_ITEM: return createDataItem();
      case StackedchartsPackage.ANNOTATION: return createAnnotation();
      case StackedchartsPackage.ZONE: return createZone();
      case StackedchartsPackage.MARKER: return createMarker();
      case StackedchartsPackage.STYLING: return createStyling();
      case StackedchartsPackage.PLAIN_STYLING: return createPlainStyling();
      case StackedchartsPackage.LINEAR_STYLING: return createLinearStyling();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
      case StackedchartsPackage.AXIS_ORIGIN:
        return createAxisOriginFromString(eDataType, initialValue);
      case StackedchartsPackage.AXIS_SCALE:
        return createAxisScaleFromString(eDataType, initialValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
      case StackedchartsPackage.AXIS_ORIGIN:
        return convertAxisOriginToString(eDataType, instanceValue);
      case StackedchartsPackage.AXIS_SCALE:
        return convertAxisScaleToString(eDataType, instanceValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ChartsSet createChartsSet()
  {
    ChartsSetImpl chartsSet = new ChartsSetImpl();
    return chartsSet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Chart createChart()
  {
    ChartImpl chart = new ChartImpl();
    return chart;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Axis createAxis()
  {
    AxisImpl axis = new AxisImpl();
    return axis;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Dataset createDataset()
  {
    DatasetImpl dataset = new DatasetImpl();
    return dataset;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DataItem createDataItem()
  {
    DataItemImpl dataItem = new DataItemImpl();
    return dataItem;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Annotation createAnnotation()
  {
    AnnotationImpl annotation = new AnnotationImpl();
    return annotation;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Zone createZone()
  {
    ZoneImpl zone = new ZoneImpl();
    return zone;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Marker createMarker()
  {
    MarkerImpl marker = new MarkerImpl();
    return marker;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Styling createStyling()
  {
    StylingImpl styling = new StylingImpl();
    return styling;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PlainStyling createPlainStyling()
  {
    PlainStylingImpl plainStyling = new PlainStylingImpl();
    return plainStyling;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LinearStyling createLinearStyling()
  {
    LinearStylingImpl linearStyling = new LinearStylingImpl();
    return linearStyling;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AxisOrigin createAxisOriginFromString(EDataType eDataType, String initialValue)
  {
    AxisOrigin result = AxisOrigin.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertAxisOriginToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AxisScale createAxisScaleFromString(EDataType eDataType, String initialValue)
  {
    AxisScale result = AxisScale.get(initialValue);
    if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertAxisScaleToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StackedchartsPackage getStackedchartsPackage()
  {
    return (StackedchartsPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static StackedchartsPackage getPackage()
  {
    return StackedchartsPackage.eINSTANCE;
  }

} //StackedchartsFactoryImpl
