/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.Annotation;
import info.limpet.stackedcharts.model.Axis;
import info.limpet.stackedcharts.model.AxisOrigin;
import info.limpet.stackedcharts.model.AxisScale;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartsSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.LinearStyling;
import info.limpet.stackedcharts.model.Marker;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.model.StackedchartsPackage;

import info.limpet.stackedcharts.model.Styling;
import info.limpet.stackedcharts.model.Zone;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class StackedchartsPackageImpl extends EPackageImpl implements StackedchartsPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass chartsSetEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass chartEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass axisEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass datasetEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass dataItemEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass annotationEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass zoneEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass markerEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass stylingEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass plainStylingEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass linearStylingEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum axisOriginEEnum = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum axisScaleEEnum = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private StackedchartsPackageImpl()
  {
    super(eNS_URI, StackedchartsFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * 
   * <p>This method is used to initialize {@link StackedchartsPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static StackedchartsPackage init()
  {
    if (isInited) return (StackedchartsPackage)EPackage.Registry.INSTANCE.getEPackage(StackedchartsPackage.eNS_URI);

    // Obtain or create and register package
    StackedchartsPackageImpl theStackedchartsPackage = (StackedchartsPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof StackedchartsPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new StackedchartsPackageImpl());

    isInited = true;

    // Create package meta-data objects
    theStackedchartsPackage.createPackageContents();

    // Initialize created meta-data
    theStackedchartsPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theStackedchartsPackage.freeze();

  
    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(StackedchartsPackage.eNS_URI, theStackedchartsPackage);
    return theStackedchartsPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getChartsSet()
  {
    return chartsSetEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getChartsSet_Charts()
  {
    return (EReference)chartsSetEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getChart()
  {
    return chartEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getChart_Parent()
  {
    return (EReference)chartEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getChart_Axes()
  {
    return (EReference)chartEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getChart_Datasets()
  {
    return (EReference)chartEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getChart_Name()
  {
    return (EAttribute)chartEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getChart_Annotations()
  {
    return (EReference)chartEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAxis()
  {
    return axisEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAxis_Parent()
  {
    return (EReference)axisEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAxis_Name()
  {
    return (EAttribute)axisEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAxis_Origin()
  {
    return (EAttribute)axisEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAxis_Scale()
  {
    return (EAttribute)axisEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDataset()
  {
    return datasetEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDataset_Name()
  {
    return (EAttribute)datasetEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDataset_Axis()
  {
    return (EReference)datasetEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDataset_Items()
  {
    return (EReference)datasetEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDataset_Styling()
  {
    return (EReference)datasetEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDataItem()
  {
    return dataItemEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDataItem_IndependentVal()
  {
    return (EAttribute)dataItemEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDataItem_DependentVal()
  {
    return (EAttribute)dataItemEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAnnotation()
  {
    return annotationEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAnnotation_Name()
  {
    return (EAttribute)annotationEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAnnotation_Color()
  {
    return (EAttribute)annotationEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAnnotation_Chart()
  {
    return (EReference)annotationEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getZone()
  {
    return zoneEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getZone_Start()
  {
    return (EAttribute)zoneEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getZone_End()
  {
    return (EAttribute)zoneEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMarker()
  {
    return markerEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getMarker_Value()
  {
    return (EAttribute)markerEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getStyling()
  {
    return stylingEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPlainStyling()
  {
    return plainStylingEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPlainStyling_Color()
  {
    return (EAttribute)plainStylingEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getLinearStyling()
  {
    return linearStylingEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getLinearStyling_StartColor()
  {
    return (EAttribute)linearStylingEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getLinearStyling_EndColor()
  {
    return (EAttribute)linearStylingEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getLinearStyling_StartVal()
  {
    return (EAttribute)linearStylingEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getLinearStyling_EndVal()
  {
    return (EAttribute)linearStylingEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getAxisOrigin()
  {
    return axisOriginEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getAxisScale()
  {
    return axisScaleEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StackedchartsFactory getStackedchartsFactory()
  {
    return (StackedchartsFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    chartsSetEClass = createEClass(CHARTS_SET);
    createEReference(chartsSetEClass, CHARTS_SET__CHARTS);

    chartEClass = createEClass(CHART);
    createEReference(chartEClass, CHART__PARENT);
    createEReference(chartEClass, CHART__AXES);
    createEReference(chartEClass, CHART__DATASETS);
    createEAttribute(chartEClass, CHART__NAME);
    createEReference(chartEClass, CHART__ANNOTATIONS);

    axisEClass = createEClass(AXIS);
    createEReference(axisEClass, AXIS__PARENT);
    createEAttribute(axisEClass, AXIS__NAME);
    createEAttribute(axisEClass, AXIS__ORIGIN);
    createEAttribute(axisEClass, AXIS__SCALE);

    datasetEClass = createEClass(DATASET);
    createEAttribute(datasetEClass, DATASET__NAME);
    createEReference(datasetEClass, DATASET__AXIS);
    createEReference(datasetEClass, DATASET__ITEMS);
    createEReference(datasetEClass, DATASET__STYLING);

    dataItemEClass = createEClass(DATA_ITEM);
    createEAttribute(dataItemEClass, DATA_ITEM__INDEPENDENT_VAL);
    createEAttribute(dataItemEClass, DATA_ITEM__DEPENDENT_VAL);

    annotationEClass = createEClass(ANNOTATION);
    createEAttribute(annotationEClass, ANNOTATION__NAME);
    createEAttribute(annotationEClass, ANNOTATION__COLOR);
    createEReference(annotationEClass, ANNOTATION__CHART);

    zoneEClass = createEClass(ZONE);
    createEAttribute(zoneEClass, ZONE__START);
    createEAttribute(zoneEClass, ZONE__END);

    markerEClass = createEClass(MARKER);
    createEAttribute(markerEClass, MARKER__VALUE);

    stylingEClass = createEClass(STYLING);

    plainStylingEClass = createEClass(PLAIN_STYLING);
    createEAttribute(plainStylingEClass, PLAIN_STYLING__COLOR);

    linearStylingEClass = createEClass(LINEAR_STYLING);
    createEAttribute(linearStylingEClass, LINEAR_STYLING__START_COLOR);
    createEAttribute(linearStylingEClass, LINEAR_STYLING__END_COLOR);
    createEAttribute(linearStylingEClass, LINEAR_STYLING__START_VAL);
    createEAttribute(linearStylingEClass, LINEAR_STYLING__END_VAL);

    // Create enums
    axisOriginEEnum = createEEnum(AXIS_ORIGIN);
    axisScaleEEnum = createEEnum(AXIS_SCALE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    zoneEClass.getESuperTypes().add(this.getAnnotation());
    markerEClass.getESuperTypes().add(this.getAnnotation());
    plainStylingEClass.getESuperTypes().add(this.getStyling());
    linearStylingEClass.getESuperTypes().add(this.getStyling());

    // Initialize classes, features, and operations; add parameters
    initEClass(chartsSetEClass, ChartsSet.class, "ChartsSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getChartsSet_Charts(), this.getChart(), this.getChart_Parent(), "charts", null, 0, -1, ChartsSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(chartEClass, Chart.class, "Chart", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getChart_Parent(), this.getChartsSet(), this.getChartsSet_Charts(), "parent", null, 0, 1, Chart.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getChart_Axes(), this.getAxis(), this.getAxis_Parent(), "axes", null, 0, -1, Chart.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getChart_Datasets(), this.getDataset(), null, "datasets", null, 0, -1, Chart.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getChart_Name(), ecorePackage.getEString(), "name", null, 0, 1, Chart.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getChart_Annotations(), this.getAnnotation(), this.getAnnotation_Chart(), "annotations", null, 0, -1, Chart.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(axisEClass, Axis.class, "Axis", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAxis_Parent(), this.getChart(), this.getChart_Axes(), "parent", null, 0, 1, Axis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAxis_Name(), ecorePackage.getEString(), "name", null, 0, 1, Axis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAxis_Origin(), this.getAxisOrigin(), "origin", null, 0, 1, Axis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAxis_Scale(), this.getAxisScale(), "scale", null, 0, 1, Axis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(datasetEClass, Dataset.class, "Dataset", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDataset_Name(), ecorePackage.getEString(), "name", null, 0, 1, Dataset.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDataset_Axis(), this.getAxis(), null, "axis", null, 1, 1, Dataset.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDataset_Items(), this.getDataItem(), null, "items", null, 0, -1, Dataset.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getDataset_Styling(), this.getStyling(), null, "styling", null, 1, 1, Dataset.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(dataItemEClass, DataItem.class, "DataItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDataItem_IndependentVal(), ecorePackage.getEDouble(), "independentVal", null, 0, 1, DataItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDataItem_DependentVal(), ecorePackage.getEDouble(), "dependentVal", null, 0, 1, DataItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(annotationEClass, Annotation.class, "Annotation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getAnnotation_Name(), ecorePackage.getEString(), "name", null, 0, 1, Annotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAnnotation_Color(), ecorePackage.getEString(), "color", null, 0, 1, Annotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAnnotation_Chart(), this.getChart(), this.getChart_Annotations(), "chart", null, 0, 1, Annotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(zoneEClass, Zone.class, "Zone", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getZone_Start(), ecorePackage.getEDouble(), "start", null, 0, 1, Zone.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getZone_End(), ecorePackage.getEDouble(), "end", null, 0, 1, Zone.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(markerEClass, Marker.class, "Marker", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getMarker_Value(), ecorePackage.getEDouble(), "value", null, 0, 1, Marker.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(stylingEClass, Styling.class, "Styling", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(plainStylingEClass, PlainStyling.class, "PlainStyling", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPlainStyling_Color(), ecorePackage.getEString(), "color", null, 0, 1, PlainStyling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(linearStylingEClass, LinearStyling.class, "LinearStyling", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getLinearStyling_StartColor(), ecorePackage.getEString(), "startColor", null, 0, 1, LinearStyling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getLinearStyling_EndColor(), ecorePackage.getEString(), "endColor", null, 0, 1, LinearStyling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getLinearStyling_StartVal(), ecorePackage.getEDouble(), "startVal", null, 0, 1, LinearStyling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getLinearStyling_EndVal(), ecorePackage.getEDouble(), "endVal", null, 0, 1, LinearStyling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(axisOriginEEnum, AxisOrigin.class, "AxisOrigin");
    addEEnumLiteral(axisOriginEEnum, AxisOrigin.MIN);
    addEEnumLiteral(axisOriginEEnum, AxisOrigin.MAX);

    initEEnum(axisScaleEEnum, AxisScale.class, "AxisScale");
    addEEnumLiteral(axisScaleEEnum, AxisScale.LINEAR);
    addEEnumLiteral(axisScaleEEnum, AxisScale.LOG);

    // Create resource
    createResource(eNS_URI);
  }

} //StackedchartsPackageImpl
