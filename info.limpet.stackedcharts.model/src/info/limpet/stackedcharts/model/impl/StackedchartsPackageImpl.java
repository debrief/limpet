/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.AbstractAnnotation;
import info.limpet.stackedcharts.model.AbstractAxis;
import info.limpet.stackedcharts.model.AxisDirection;
import info.limpet.stackedcharts.model.AxisScale;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.LineType;
import info.limpet.stackedcharts.model.LinearStyling;
import info.limpet.stackedcharts.model.Marker;
import info.limpet.stackedcharts.model.MarkerStyle;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.ScatterSet;
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

import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class StackedchartsPackageImpl extends EPackageImpl implements StackedchartsPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass chartSetEClass = null;

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
	private EClass dependentAxisEClass = null;

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
	private EClass abstractAnnotationEClass = null;

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
	private EClass abstractAxisEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass independentAxisEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass scatterSetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass datumEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum axisScaleEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum orientationEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum axisDirectionEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum markerStyleEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum lineTypeEEnum = null;

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
	private StackedchartsPackageImpl() {
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
	public static StackedchartsPackage init() {
		if (isInited) return (StackedchartsPackage)EPackage.Registry.INSTANCE.getEPackage(StackedchartsPackage.eNS_URI);

		// Obtain or create and register package
		StackedchartsPackageImpl theStackedchartsPackage = (StackedchartsPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof StackedchartsPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new StackedchartsPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		XMLTypePackage.eINSTANCE.eClass();

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
	public EClass getChartSet() {
		return chartSetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getChartSet_Charts() {
		return (EReference)chartSetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getChartSet_Orientation() {
		return (EAttribute)chartSetEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getChartSet_SharedAxis() {
		return (EReference)chartSetEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getChartSet_Datasets() {
		return (EReference)chartSetEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getChart() {
		return chartEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getChart_Parent() {
		return (EReference)chartEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getChart_MaxAxes() {
		return (EReference)chartEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getChart_Name() {
		return (EAttribute)chartEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getChart_Title() {
		return (EAttribute)chartEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getChart_MinAxes() {
		return (EReference)chartEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDependentAxis() {
		return dependentAxisEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDependentAxis_Datasets() {
		return (EReference)dependentAxisEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDataset() {
		return datasetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDataset_Name() {
		return (EAttribute)datasetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDataset_Measurements() {
		return (EReference)datasetEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDataset_Styling() {
		return (EReference)datasetEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDataItem() {
		return dataItemEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDataItem_IndependentVal() {
		return (EAttribute)dataItemEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDataItem_DependentVal() {
		return (EAttribute)dataItemEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAbstractAnnotation() {
		return abstractAnnotationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAbstractAnnotation_Name() {
		return (EAttribute)abstractAnnotationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAbstractAnnotation_Color() {
		return (EAttribute)abstractAnnotationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getZone() {
		return zoneEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getZone_Start() {
		return (EAttribute)zoneEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getZone_End() {
		return (EAttribute)zoneEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMarker() {
		return markerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMarker_Value() {
		return (EAttribute)markerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getStyling() {
		return stylingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getStyling_MarkerStyle() {
		return (EAttribute)stylingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getStyling_MarkerSize() {
		return (EAttribute)stylingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getStyling_LineThickness() {
		return (EAttribute)stylingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getStyling_LineStyle() {
		return (EAttribute)stylingEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPlainStyling() {
		return plainStylingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPlainStyling_Color() {
		return (EAttribute)plainStylingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLinearStyling() {
		return linearStylingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLinearStyling_StartColor() {
		return (EAttribute)linearStylingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLinearStyling_EndColor() {
		return (EAttribute)linearStylingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLinearStyling_StartVal() {
		return (EAttribute)linearStylingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLinearStyling_EndVal() {
		return (EAttribute)linearStylingEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getAbstractAxis() {
		return abstractAxisEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAbstractAxis_Scale() {
		return (EAttribute)abstractAxisEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAbstractAxis_Name() {
		return (EAttribute)abstractAxisEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAbstractAxis_AutoScale() {
		return (EAttribute)abstractAxisEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAbstractAxis_Direction() {
		return (EAttribute)abstractAxisEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getAbstractAxis_Annotations() {
		return (EReference)abstractAxisEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAbstractAxis_Font() {
		return (EAttribute)abstractAxisEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getAbstractAxis_Color() {
		return (EAttribute)abstractAxisEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getIndependentAxis() {
		return independentAxisEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getScatterSet() {
		return scatterSetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getScatterSet_Datums() {
		return (EReference)scatterSetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDatum() {
		return datumEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDatum_Val() {
		return (EAttribute)datumEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getAxisScale() {
		return axisScaleEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getOrientation() {
		return orientationEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getAxisDirection() {
		return axisDirectionEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getMarkerStyle() {
		return markerStyleEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getLineType() {
		return lineTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StackedchartsFactory getStackedchartsFactory() {
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
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		chartSetEClass = createEClass(CHART_SET);
		createEReference(chartSetEClass, CHART_SET__CHARTS);
		createEAttribute(chartSetEClass, CHART_SET__ORIENTATION);
		createEReference(chartSetEClass, CHART_SET__SHARED_AXIS);
		createEReference(chartSetEClass, CHART_SET__DATASETS);

		chartEClass = createEClass(CHART);
		createEReference(chartEClass, CHART__PARENT);
		createEReference(chartEClass, CHART__MAX_AXES);
		createEAttribute(chartEClass, CHART__NAME);
		createEAttribute(chartEClass, CHART__TITLE);
		createEReference(chartEClass, CHART__MIN_AXES);

		dependentAxisEClass = createEClass(DEPENDENT_AXIS);
		createEReference(dependentAxisEClass, DEPENDENT_AXIS__DATASETS);

		datasetEClass = createEClass(DATASET);
		createEAttribute(datasetEClass, DATASET__NAME);
		createEReference(datasetEClass, DATASET__MEASUREMENTS);
		createEReference(datasetEClass, DATASET__STYLING);

		dataItemEClass = createEClass(DATA_ITEM);
		createEAttribute(dataItemEClass, DATA_ITEM__INDEPENDENT_VAL);
		createEAttribute(dataItemEClass, DATA_ITEM__DEPENDENT_VAL);

		abstractAnnotationEClass = createEClass(ABSTRACT_ANNOTATION);
		createEAttribute(abstractAnnotationEClass, ABSTRACT_ANNOTATION__NAME);
		createEAttribute(abstractAnnotationEClass, ABSTRACT_ANNOTATION__COLOR);

		zoneEClass = createEClass(ZONE);
		createEAttribute(zoneEClass, ZONE__START);
		createEAttribute(zoneEClass, ZONE__END);

		markerEClass = createEClass(MARKER);
		createEAttribute(markerEClass, MARKER__VALUE);

		stylingEClass = createEClass(STYLING);
		createEAttribute(stylingEClass, STYLING__MARKER_STYLE);
		createEAttribute(stylingEClass, STYLING__MARKER_SIZE);
		createEAttribute(stylingEClass, STYLING__LINE_THICKNESS);
		createEAttribute(stylingEClass, STYLING__LINE_STYLE);

		plainStylingEClass = createEClass(PLAIN_STYLING);
		createEAttribute(plainStylingEClass, PLAIN_STYLING__COLOR);

		linearStylingEClass = createEClass(LINEAR_STYLING);
		createEAttribute(linearStylingEClass, LINEAR_STYLING__START_COLOR);
		createEAttribute(linearStylingEClass, LINEAR_STYLING__END_COLOR);
		createEAttribute(linearStylingEClass, LINEAR_STYLING__START_VAL);
		createEAttribute(linearStylingEClass, LINEAR_STYLING__END_VAL);

		abstractAxisEClass = createEClass(ABSTRACT_AXIS);
		createEAttribute(abstractAxisEClass, ABSTRACT_AXIS__SCALE);
		createEAttribute(abstractAxisEClass, ABSTRACT_AXIS__NAME);
		createEAttribute(abstractAxisEClass, ABSTRACT_AXIS__AUTO_SCALE);
		createEAttribute(abstractAxisEClass, ABSTRACT_AXIS__DIRECTION);
		createEAttribute(abstractAxisEClass, ABSTRACT_AXIS__FONT);
		createEAttribute(abstractAxisEClass, ABSTRACT_AXIS__COLOR);
		createEReference(abstractAxisEClass, ABSTRACT_AXIS__ANNOTATIONS);

		independentAxisEClass = createEClass(INDEPENDENT_AXIS);

		scatterSetEClass = createEClass(SCATTER_SET);
		createEReference(scatterSetEClass, SCATTER_SET__DATUMS);

		datumEClass = createEClass(DATUM);
		createEAttribute(datumEClass, DATUM__VAL);

		// Create enums
		axisScaleEEnum = createEEnum(AXIS_SCALE);
		orientationEEnum = createEEnum(ORIENTATION);
		axisDirectionEEnum = createEEnum(AXIS_DIRECTION);
		markerStyleEEnum = createEEnum(MARKER_STYLE);
		lineTypeEEnum = createEEnum(LINE_TYPE);
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
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		XMLTypePackage theXMLTypePackage = (XMLTypePackage)EPackage.Registry.INSTANCE.getEPackage(XMLTypePackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		dependentAxisEClass.getESuperTypes().add(this.getAbstractAxis());
		zoneEClass.getESuperTypes().add(this.getAbstractAnnotation());
		markerEClass.getESuperTypes().add(this.getAbstractAnnotation());
		plainStylingEClass.getESuperTypes().add(this.getStyling());
		linearStylingEClass.getESuperTypes().add(this.getStyling());
		independentAxisEClass.getESuperTypes().add(this.getAbstractAxis());
		scatterSetEClass.getESuperTypes().add(this.getAbstractAnnotation());

		// Initialize classes, features, and operations; add parameters
		initEClass(chartSetEClass, ChartSet.class, "ChartSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getChartSet_Charts(), this.getChart(), this.getChart_Parent(), "charts", null, 0, -1, ChartSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChartSet_Orientation(), this.getOrientation(), "orientation", "Horizontal", 0, 1, ChartSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getChartSet_SharedAxis(), this.getIndependentAxis(), null, "sharedAxis", null, 0, 1, ChartSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getChartSet_Datasets(), this.getDataset(), null, "datasets", null, 0, -1, ChartSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(chartEClass, Chart.class, "Chart", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getChart_Parent(), this.getChartSet(), this.getChartSet_Charts(), "parent", null, 0, 1, Chart.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getChart_MaxAxes(), this.getDependentAxis(), null, "maxAxes", null, 0, -1, Chart.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChart_Name(), ecorePackage.getEString(), "name", null, 0, 1, Chart.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getChart_Title(), ecorePackage.getEString(), "title", null, 0, 1, Chart.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getChart_MinAxes(), this.getDependentAxis(), null, "minAxes", null, 0, -1, Chart.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dependentAxisEClass, DependentAxis.class, "DependentAxis", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDependentAxis_Datasets(), this.getDataset(), null, "datasets", null, 0, -1, DependentAxis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(datasetEClass, Dataset.class, "Dataset", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDataset_Name(), ecorePackage.getEString(), "name", null, 0, 1, Dataset.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDataset_Measurements(), this.getDataItem(), null, "measurements", null, 0, -1, Dataset.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDataset_Styling(), this.getStyling(), null, "styling", null, 1, 1, Dataset.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(dataItemEClass, DataItem.class, "DataItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDataItem_IndependentVal(), ecorePackage.getEDouble(), "independentVal", null, 0, 1, DataItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDataItem_DependentVal(), ecorePackage.getEDouble(), "dependentVal", null, 0, 1, DataItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(abstractAnnotationEClass, AbstractAnnotation.class, "AbstractAnnotation", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAbstractAnnotation_Name(), ecorePackage.getEString(), "name", null, 0, 1, AbstractAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAbstractAnnotation_Color(), ecorePackage.getEString(), "color", null, 0, 1, AbstractAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(zoneEClass, Zone.class, "Zone", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getZone_Start(), ecorePackage.getEDouble(), "start", null, 0, 1, Zone.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getZone_End(), ecorePackage.getEDouble(), "end", null, 0, 1, Zone.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(markerEClass, Marker.class, "Marker", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMarker_Value(), ecorePackage.getEDouble(), "value", null, 0, 1, Marker.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(stylingEClass, Styling.class, "Styling", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getStyling_MarkerStyle(), this.getMarkerStyle(), "markerStyle", null, 0, 1, Styling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getStyling_MarkerSize(), theXMLTypePackage.getDouble(), "markerSize", null, 0, 1, Styling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getStyling_LineThickness(), theXMLTypePackage.getDouble(), "lineThickness", "0.0", 0, 1, Styling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getStyling_LineStyle(), this.getLineType(), "lineStyle", null, 0, 1, Styling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(plainStylingEClass, PlainStyling.class, "PlainStyling", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPlainStyling_Color(), ecorePackage.getEString(), "color", null, 0, 1, PlainStyling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(linearStylingEClass, LinearStyling.class, "LinearStyling", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getLinearStyling_StartColor(), ecorePackage.getEString(), "startColor", null, 0, 1, LinearStyling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLinearStyling_EndColor(), ecorePackage.getEString(), "endColor", null, 0, 1, LinearStyling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLinearStyling_StartVal(), ecorePackage.getEDouble(), "startVal", null, 0, 1, LinearStyling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLinearStyling_EndVal(), ecorePackage.getEDouble(), "endVal", null, 0, 1, LinearStyling.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(abstractAxisEClass, AbstractAxis.class, "AbstractAxis", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAbstractAxis_Scale(), this.getAxisScale(), "scale", null, 0, 1, AbstractAxis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAbstractAxis_Name(), ecorePackage.getEString(), "name", null, 0, 1, AbstractAxis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAbstractAxis_AutoScale(), theXMLTypePackage.getBoolean(), "autoScale", null, 0, 1, AbstractAxis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAbstractAxis_Direction(), this.getAxisDirection(), "direction", null, 0, 1, AbstractAxis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAbstractAxis_Font(), ecorePackage.getEString(), "font", null, 0, 1, AbstractAxis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAbstractAxis_Color(), ecorePackage.getEString(), "color", null, 0, 1, AbstractAxis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAbstractAxis_Annotations(), this.getAbstractAnnotation(), null, "annotations", null, 0, -1, AbstractAxis.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(independentAxisEClass, IndependentAxis.class, "IndependentAxis", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(scatterSetEClass, ScatterSet.class, "ScatterSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getScatterSet_Datums(), this.getDatum(), null, "datums", null, 0, -1, ScatterSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(datumEClass, Datum.class, "Datum", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDatum_Val(), ecorePackage.getEDouble(), "val", null, 0, 1, Datum.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(axisScaleEEnum, AxisScale.class, "AxisScale");
		addEEnumLiteral(axisScaleEEnum, AxisScale.LINEAR);
		addEEnumLiteral(axisScaleEEnum, AxisScale.LOG);

		initEEnum(orientationEEnum, Orientation.class, "Orientation");
		addEEnumLiteral(orientationEEnum, Orientation.HORIZONTAL);
		addEEnumLiteral(orientationEEnum, Orientation.VERTICAL);

		initEEnum(axisDirectionEEnum, AxisDirection.class, "AxisDirection");
		addEEnumLiteral(axisDirectionEEnum, AxisDirection.ASCENDING);
		addEEnumLiteral(axisDirectionEEnum, AxisDirection.DESCENDING);

		initEEnum(markerStyleEEnum, MarkerStyle.class, "MarkerStyle");
		addEEnumLiteral(markerStyleEEnum, MarkerStyle.CIRCLE);
		addEEnumLiteral(markerStyleEEnum, MarkerStyle.SQUARE);
		addEEnumLiteral(markerStyleEEnum, MarkerStyle.NONE);

		initEEnum(lineTypeEEnum, LineType.class, "LineType");
		addEEnumLiteral(lineTypeEEnum, LineType.NONE);
		addEEnumLiteral(lineTypeEEnum, LineType.SOLID);
		addEEnumLiteral(lineTypeEEnum, LineType.DOTTED);
		addEEnumLiteral(lineTypeEEnum, LineType.DASHED);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http:///org/eclipse/emf/ecore/util/ExtendedMetaData
		createExtendedMetaDataAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createExtendedMetaDataAnnotations() {
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";	
		addAnnotation
		  (getChartSet_Charts(), 
		   source, 
		   new String[] {
			 "name", "chart"
		   });
	}

} //StackedchartsPackageImpl
