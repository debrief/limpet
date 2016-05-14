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
public class StackedchartsFactoryImpl extends EFactoryImpl implements StackedchartsFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static StackedchartsFactory init() {
		try {
			StackedchartsFactory theStackedchartsFactory = (StackedchartsFactory)EPackage.Registry.INSTANCE.getEFactory(StackedchartsPackage.eNS_URI);
			if (theStackedchartsFactory != null) {
				return theStackedchartsFactory;
			}
		}
		catch (Exception exception) {
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
	public StackedchartsFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case StackedchartsPackage.CHART_SET: return createChartSet();
			case StackedchartsPackage.CHART: return createChart();
			case StackedchartsPackage.DEPENDENT_AXIS: return createDependentAxis();
			case StackedchartsPackage.DATASET: return createDataset();
			case StackedchartsPackage.DATA_ITEM: return createDataItem();
			case StackedchartsPackage.ZONE: return createZone();
			case StackedchartsPackage.MARKER: return createMarker();
			case StackedchartsPackage.STYLING: return createStyling();
			case StackedchartsPackage.PLAIN_STYLING: return createPlainStyling();
			case StackedchartsPackage.LINEAR_STYLING: return createLinearStyling();
			case StackedchartsPackage.INDEPENDENT_AXIS: return createIndependentAxis();
			case StackedchartsPackage.SCATTER_SET: return createScatterSet();
			case StackedchartsPackage.DATUM: return createDatum();
			case StackedchartsPackage.SELECTIVE_ANNOTATION: return createSelectiveAnnotation();
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
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case StackedchartsPackage.AXIS_SCALE:
				return createAxisScaleFromString(eDataType, initialValue);
			case StackedchartsPackage.ORIENTATION:
				return createOrientationFromString(eDataType, initialValue);
			case StackedchartsPackage.AXIS_DIRECTION:
				return createAxisDirectionFromString(eDataType, initialValue);
			case StackedchartsPackage.MARKER_STYLE:
				return createMarkerStyleFromString(eDataType, initialValue);
			case StackedchartsPackage.LINE_TYPE:
				return createLineTypeFromString(eDataType, initialValue);
			case StackedchartsPackage.AXIS_TYPE:
				return createAxisTypeFromString(eDataType, initialValue);
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
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case StackedchartsPackage.AXIS_SCALE:
				return convertAxisScaleToString(eDataType, instanceValue);
			case StackedchartsPackage.ORIENTATION:
				return convertOrientationToString(eDataType, instanceValue);
			case StackedchartsPackage.AXIS_DIRECTION:
				return convertAxisDirectionToString(eDataType, instanceValue);
			case StackedchartsPackage.MARKER_STYLE:
				return convertMarkerStyleToString(eDataType, instanceValue);
			case StackedchartsPackage.LINE_TYPE:
				return convertLineTypeToString(eDataType, instanceValue);
			case StackedchartsPackage.AXIS_TYPE:
				return convertAxisTypeToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ChartSet createChartSet() {
		ChartSetImpl chartSet = new ChartSetImpl();
		return chartSet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Chart createChart() {
		ChartImpl chart = new ChartImpl();
		return chart;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DependentAxis createDependentAxis() {
		DependentAxisImpl dependentAxis = new DependentAxisImpl();
		return dependentAxis;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Dataset createDataset() {
		DatasetImpl dataset = new DatasetImpl();
		return dataset;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataItem createDataItem() {
		DataItemImpl dataItem = new DataItemImpl();
		return dataItem;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Zone createZone() {
		ZoneImpl zone = new ZoneImpl();
		return zone;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Marker createMarker() {
		MarkerImpl marker = new MarkerImpl();
		return marker;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Styling createStyling() {
		StylingImpl styling = new StylingImpl();
		return styling;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PlainStyling createPlainStyling() {
		PlainStylingImpl plainStyling = new PlainStylingImpl();
		return plainStyling;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LinearStyling createLinearStyling() {
		LinearStylingImpl linearStyling = new LinearStylingImpl();
		return linearStyling;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IndependentAxis createIndependentAxis() {
		IndependentAxisImpl independentAxis = new IndependentAxisImpl();
		return independentAxis;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ScatterSet createScatterSet() {
		ScatterSetImpl scatterSet = new ScatterSetImpl();
		return scatterSet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Datum createDatum() {
		DatumImpl datum = new DatumImpl();
		return datum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SelectiveAnnotation createSelectiveAnnotation() {
		SelectiveAnnotationImpl selectiveAnnotation = new SelectiveAnnotationImpl();
		return selectiveAnnotation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AxisScale createAxisScaleFromString(EDataType eDataType, String initialValue) {
		AxisScale result = AxisScale.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertAxisScaleToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Orientation createOrientationFromString(EDataType eDataType, String initialValue) {
		Orientation result = Orientation.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertOrientationToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AxisDirection createAxisDirectionFromString(EDataType eDataType, String initialValue) {
		AxisDirection result = AxisDirection.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertAxisDirectionToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MarkerStyle createMarkerStyleFromString(EDataType eDataType, String initialValue) {
		MarkerStyle result = MarkerStyle.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertMarkerStyleToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LineType createLineTypeFromString(EDataType eDataType, String initialValue) {
		LineType result = LineType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertLineTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AxisType createAxisTypeFromString(EDataType eDataType, String initialValue) {
		AxisType result = AxisType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertAxisTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StackedchartsPackage getStackedchartsPackage() {
		return (StackedchartsPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static StackedchartsPackage getPackage() {
		return StackedchartsPackage.eINSTANCE;
	}

} //StackedchartsFactoryImpl
