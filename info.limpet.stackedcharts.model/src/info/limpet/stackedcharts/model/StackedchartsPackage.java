/**
 */
package info.limpet.stackedcharts.model;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see info.limpet.stackedcharts.model.StackedchartsFactory
 * @model kind="package"
 * @generated
 */
public interface StackedchartsPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "model";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "stackedcharts";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "stackedcharts";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  StackedchartsPackage eINSTANCE = info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl.init();

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.ChartSetImpl <em>Chart Set</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see info.limpet.stackedcharts.model.impl.ChartSetImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getChartSet()
   * @generated
   */
  int CHART_SET = 0;

  /**
   * The feature id for the '<em><b>Charts</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHART_SET__CHARTS = 0;

  /**
   * The number of structural features of the '<em>Chart Set</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHART_SET_FEATURE_COUNT = 1;

  /**
   * The number of operations of the '<em>Chart Set</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHART_SET_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.ChartImpl <em>Chart</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see info.limpet.stackedcharts.model.impl.ChartImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getChart()
   * @generated
   */
  int CHART = 1;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHART__PARENT = 0;

  /**
   * The feature id for the '<em><b>Axes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHART__AXES = 1;

  /**
   * The feature id for the '<em><b>Datasets</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHART__DATASETS = 2;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHART__NAME = 3;

  /**
   * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHART__ANNOTATIONS = 4;

  /**
   * The number of structural features of the '<em>Chart</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHART_FEATURE_COUNT = 5;

  /**
   * The number of operations of the '<em>Chart</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHART_OPERATION_COUNT = 0;


  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.AxisImpl <em>Axis</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see info.limpet.stackedcharts.model.impl.AxisImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAxis()
   * @generated
   */
  int AXIS = 2;

  /**
   * The feature id for the '<em><b>Parent</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AXIS__PARENT = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AXIS__NAME = 1;

  /**
   * The feature id for the '<em><b>Origin</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AXIS__ORIGIN = 2;

  /**
   * The feature id for the '<em><b>Scale</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AXIS__SCALE = 3;

  /**
   * The number of structural features of the '<em>Axis</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AXIS_FEATURE_COUNT = 4;

  /**
   * The number of operations of the '<em>Axis</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AXIS_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.DatasetImpl <em>Dataset</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see info.limpet.stackedcharts.model.impl.DatasetImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getDataset()
   * @generated
   */
  int DATASET = 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATASET__NAME = 0;

  /**
   * The feature id for the '<em><b>Axis</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATASET__AXIS = 1;

  /**
   * The feature id for the '<em><b>Items</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATASET__ITEMS = 2;

  /**
   * The feature id for the '<em><b>Styling</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATASET__STYLING = 3;

  /**
   * The number of structural features of the '<em>Dataset</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATASET_FEATURE_COUNT = 4;

  /**
   * The number of operations of the '<em>Dataset</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATASET_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.DataItemImpl <em>Data Item</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see info.limpet.stackedcharts.model.impl.DataItemImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getDataItem()
   * @generated
   */
  int DATA_ITEM = 4;

  /**
   * The feature id for the '<em><b>Independent Val</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_ITEM__INDEPENDENT_VAL = 0;

  /**
   * The feature id for the '<em><b>Dependent Val</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_ITEM__DEPENDENT_VAL = 1;

  /**
   * The number of structural features of the '<em>Data Item</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_ITEM_FEATURE_COUNT = 2;

  /**
   * The number of operations of the '<em>Data Item</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_ITEM_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.AnnotationImpl <em>Annotation</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see info.limpet.stackedcharts.model.impl.AnnotationImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAnnotation()
   * @generated
   */
  int ANNOTATION = 5;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ANNOTATION__NAME = 0;

  /**
   * The feature id for the '<em><b>Color</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ANNOTATION__COLOR = 1;

  /**
   * The feature id for the '<em><b>Chart</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ANNOTATION__CHART = 2;

  /**
   * The number of structural features of the '<em>Annotation</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ANNOTATION_FEATURE_COUNT = 3;

  /**
   * The number of operations of the '<em>Annotation</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ANNOTATION_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.ZoneImpl <em>Zone</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see info.limpet.stackedcharts.model.impl.ZoneImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getZone()
   * @generated
   */
  int ZONE = 6;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ZONE__NAME = ANNOTATION__NAME;

  /**
   * The feature id for the '<em><b>Color</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ZONE__COLOR = ANNOTATION__COLOR;

  /**
   * The feature id for the '<em><b>Chart</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ZONE__CHART = ANNOTATION__CHART;

  /**
   * The feature id for the '<em><b>Start</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ZONE__START = ANNOTATION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>End</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ZONE__END = ANNOTATION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Zone</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ZONE_FEATURE_COUNT = ANNOTATION_FEATURE_COUNT + 2;

  /**
   * The number of operations of the '<em>Zone</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ZONE_OPERATION_COUNT = ANNOTATION_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.MarkerImpl <em>Marker</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see info.limpet.stackedcharts.model.impl.MarkerImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getMarker()
   * @generated
   */
  int MARKER = 7;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MARKER__NAME = ANNOTATION__NAME;

  /**
   * The feature id for the '<em><b>Color</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MARKER__COLOR = ANNOTATION__COLOR;

  /**
   * The feature id for the '<em><b>Chart</b></em>' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MARKER__CHART = ANNOTATION__CHART;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MARKER__VALUE = ANNOTATION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Marker</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MARKER_FEATURE_COUNT = ANNOTATION_FEATURE_COUNT + 1;

  /**
   * The number of operations of the '<em>Marker</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MARKER_OPERATION_COUNT = ANNOTATION_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.StylingImpl <em>Styling</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see info.limpet.stackedcharts.model.impl.StylingImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getStyling()
   * @generated
   */
  int STYLING = 8;

  /**
   * The number of structural features of the '<em>Styling</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STYLING_FEATURE_COUNT = 0;

  /**
   * The number of operations of the '<em>Styling</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STYLING_OPERATION_COUNT = 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.PlainStylingImpl <em>Plain Styling</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see info.limpet.stackedcharts.model.impl.PlainStylingImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getPlainStyling()
   * @generated
   */
  int PLAIN_STYLING = 9;

  /**
   * The feature id for the '<em><b>Color</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PLAIN_STYLING__COLOR = STYLING_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Plain Styling</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PLAIN_STYLING_FEATURE_COUNT = STYLING_FEATURE_COUNT + 1;

  /**
   * The number of operations of the '<em>Plain Styling</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PLAIN_STYLING_OPERATION_COUNT = STYLING_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.impl.LinearStylingImpl <em>Linear Styling</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see info.limpet.stackedcharts.model.impl.LinearStylingImpl
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getLinearStyling()
   * @generated
   */
  int LINEAR_STYLING = 10;

  /**
   * The feature id for the '<em><b>Start Color</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINEAR_STYLING__START_COLOR = STYLING_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>End Color</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINEAR_STYLING__END_COLOR = STYLING_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Start Val</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINEAR_STYLING__START_VAL = STYLING_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>End Val</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINEAR_STYLING__END_VAL = STYLING_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Linear Styling</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINEAR_STYLING_FEATURE_COUNT = STYLING_FEATURE_COUNT + 4;

  /**
   * The number of operations of the '<em>Linear Styling</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LINEAR_STYLING_OPERATION_COUNT = STYLING_OPERATION_COUNT + 0;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.AxisOrigin <em>Axis Origin</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see info.limpet.stackedcharts.model.AxisOrigin
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAxisOrigin()
   * @generated
   */
  int AXIS_ORIGIN = 11;

  /**
   * The meta object id for the '{@link info.limpet.stackedcharts.model.AxisScale <em>Axis Scale</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see info.limpet.stackedcharts.model.AxisScale
   * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAxisScale()
   * @generated
   */
  int AXIS_SCALE = 12;


  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.ChartSet <em>Chart Set</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Chart Set</em>'.
   * @see info.limpet.stackedcharts.model.ChartSet
   * @generated
   */
  EClass getChartSet();

  /**
   * Returns the meta object for the containment reference list '{@link info.limpet.stackedcharts.model.ChartSet#getCharts <em>Charts</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Charts</em>'.
   * @see info.limpet.stackedcharts.model.ChartSet#getCharts()
   * @see #getChartSet()
   * @generated
   */
  EReference getChartSet_Charts();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.Chart <em>Chart</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Chart</em>'.
   * @see info.limpet.stackedcharts.model.Chart
   * @generated
   */
  EClass getChart();

  /**
   * Returns the meta object for the container reference '{@link info.limpet.stackedcharts.model.Chart#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Parent</em>'.
   * @see info.limpet.stackedcharts.model.Chart#getParent()
   * @see #getChart()
   * @generated
   */
  EReference getChart_Parent();

  /**
   * Returns the meta object for the containment reference list '{@link info.limpet.stackedcharts.model.Chart#getAxes <em>Axes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Axes</em>'.
   * @see info.limpet.stackedcharts.model.Chart#getAxes()
   * @see #getChart()
   * @generated
   */
  EReference getChart_Axes();

  /**
   * Returns the meta object for the containment reference list '{@link info.limpet.stackedcharts.model.Chart#getDatasets <em>Datasets</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Datasets</em>'.
   * @see info.limpet.stackedcharts.model.Chart#getDatasets()
   * @see #getChart()
   * @generated
   */
  EReference getChart_Datasets();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.Chart#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see info.limpet.stackedcharts.model.Chart#getName()
   * @see #getChart()
   * @generated
   */
  EAttribute getChart_Name();

  /**
   * Returns the meta object for the containment reference list '{@link info.limpet.stackedcharts.model.Chart#getAnnotations <em>Annotations</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Annotations</em>'.
   * @see info.limpet.stackedcharts.model.Chart#getAnnotations()
   * @see #getChart()
   * @generated
   */
  EReference getChart_Annotations();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.Axis <em>Axis</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Axis</em>'.
   * @see info.limpet.stackedcharts.model.Axis
   * @generated
   */
  EClass getAxis();

  /**
   * Returns the meta object for the container reference '{@link info.limpet.stackedcharts.model.Axis#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Parent</em>'.
   * @see info.limpet.stackedcharts.model.Axis#getParent()
   * @see #getAxis()
   * @generated
   */
  EReference getAxis_Parent();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.Axis#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see info.limpet.stackedcharts.model.Axis#getName()
   * @see #getAxis()
   * @generated
   */
  EAttribute getAxis_Name();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.Axis#getOrigin <em>Origin</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Origin</em>'.
   * @see info.limpet.stackedcharts.model.Axis#getOrigin()
   * @see #getAxis()
   * @generated
   */
  EAttribute getAxis_Origin();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.Axis#getScale <em>Scale</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Scale</em>'.
   * @see info.limpet.stackedcharts.model.Axis#getScale()
   * @see #getAxis()
   * @generated
   */
  EAttribute getAxis_Scale();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.Dataset <em>Dataset</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Dataset</em>'.
   * @see info.limpet.stackedcharts.model.Dataset
   * @generated
   */
  EClass getDataset();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.Dataset#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see info.limpet.stackedcharts.model.Dataset#getName()
   * @see #getDataset()
   * @generated
   */
  EAttribute getDataset_Name();

  /**
   * Returns the meta object for the reference '{@link info.limpet.stackedcharts.model.Dataset#getAxis <em>Axis</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Axis</em>'.
   * @see info.limpet.stackedcharts.model.Dataset#getAxis()
   * @see #getDataset()
   * @generated
   */
  EReference getDataset_Axis();

  /**
   * Returns the meta object for the containment reference list '{@link info.limpet.stackedcharts.model.Dataset#getItems <em>Items</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Items</em>'.
   * @see info.limpet.stackedcharts.model.Dataset#getItems()
   * @see #getDataset()
   * @generated
   */
  EReference getDataset_Items();

  /**
   * Returns the meta object for the containment reference '{@link info.limpet.stackedcharts.model.Dataset#getStyling <em>Styling</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Styling</em>'.
   * @see info.limpet.stackedcharts.model.Dataset#getStyling()
   * @see #getDataset()
   * @generated
   */
  EReference getDataset_Styling();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.DataItem <em>Data Item</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Data Item</em>'.
   * @see info.limpet.stackedcharts.model.DataItem
   * @generated
   */
  EClass getDataItem();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.DataItem#getIndependentVal <em>Independent Val</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Independent Val</em>'.
   * @see info.limpet.stackedcharts.model.DataItem#getIndependentVal()
   * @see #getDataItem()
   * @generated
   */
  EAttribute getDataItem_IndependentVal();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.DataItem#getDependentVal <em>Dependent Val</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Dependent Val</em>'.
   * @see info.limpet.stackedcharts.model.DataItem#getDependentVal()
   * @see #getDataItem()
   * @generated
   */
  EAttribute getDataItem_DependentVal();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.Annotation <em>Annotation</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Annotation</em>'.
   * @see info.limpet.stackedcharts.model.Annotation
   * @generated
   */
  EClass getAnnotation();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.Annotation#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see info.limpet.stackedcharts.model.Annotation#getName()
   * @see #getAnnotation()
   * @generated
   */
  EAttribute getAnnotation_Name();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.Annotation#getColor <em>Color</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Color</em>'.
   * @see info.limpet.stackedcharts.model.Annotation#getColor()
   * @see #getAnnotation()
   * @generated
   */
  EAttribute getAnnotation_Color();

  /**
   * Returns the meta object for the container reference '{@link info.limpet.stackedcharts.model.Annotation#getChart <em>Chart</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the container reference '<em>Chart</em>'.
   * @see info.limpet.stackedcharts.model.Annotation#getChart()
   * @see #getAnnotation()
   * @generated
   */
  EReference getAnnotation_Chart();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.Zone <em>Zone</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Zone</em>'.
   * @see info.limpet.stackedcharts.model.Zone
   * @generated
   */
  EClass getZone();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.Zone#getStart <em>Start</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Start</em>'.
   * @see info.limpet.stackedcharts.model.Zone#getStart()
   * @see #getZone()
   * @generated
   */
  EAttribute getZone_Start();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.Zone#getEnd <em>End</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>End</em>'.
   * @see info.limpet.stackedcharts.model.Zone#getEnd()
   * @see #getZone()
   * @generated
   */
  EAttribute getZone_End();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.Marker <em>Marker</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Marker</em>'.
   * @see info.limpet.stackedcharts.model.Marker
   * @generated
   */
  EClass getMarker();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.Marker#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see info.limpet.stackedcharts.model.Marker#getValue()
   * @see #getMarker()
   * @generated
   */
  EAttribute getMarker_Value();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.Styling <em>Styling</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Styling</em>'.
   * @see info.limpet.stackedcharts.model.Styling
   * @generated
   */
  EClass getStyling();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.PlainStyling <em>Plain Styling</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Plain Styling</em>'.
   * @see info.limpet.stackedcharts.model.PlainStyling
   * @generated
   */
  EClass getPlainStyling();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.PlainStyling#getColor <em>Color</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Color</em>'.
   * @see info.limpet.stackedcharts.model.PlainStyling#getColor()
   * @see #getPlainStyling()
   * @generated
   */
  EAttribute getPlainStyling_Color();

  /**
   * Returns the meta object for class '{@link info.limpet.stackedcharts.model.LinearStyling <em>Linear Styling</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Linear Styling</em>'.
   * @see info.limpet.stackedcharts.model.LinearStyling
   * @generated
   */
  EClass getLinearStyling();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.LinearStyling#getStartColor <em>Start Color</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Start Color</em>'.
   * @see info.limpet.stackedcharts.model.LinearStyling#getStartColor()
   * @see #getLinearStyling()
   * @generated
   */
  EAttribute getLinearStyling_StartColor();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.LinearStyling#getEndColor <em>End Color</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>End Color</em>'.
   * @see info.limpet.stackedcharts.model.LinearStyling#getEndColor()
   * @see #getLinearStyling()
   * @generated
   */
  EAttribute getLinearStyling_EndColor();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.LinearStyling#getStartVal <em>Start Val</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Start Val</em>'.
   * @see info.limpet.stackedcharts.model.LinearStyling#getStartVal()
   * @see #getLinearStyling()
   * @generated
   */
  EAttribute getLinearStyling_StartVal();

  /**
   * Returns the meta object for the attribute '{@link info.limpet.stackedcharts.model.LinearStyling#getEndVal <em>End Val</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>End Val</em>'.
   * @see info.limpet.stackedcharts.model.LinearStyling#getEndVal()
   * @see #getLinearStyling()
   * @generated
   */
  EAttribute getLinearStyling_EndVal();

  /**
   * Returns the meta object for enum '{@link info.limpet.stackedcharts.model.AxisOrigin <em>Axis Origin</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Axis Origin</em>'.
   * @see info.limpet.stackedcharts.model.AxisOrigin
   * @generated
   */
  EEnum getAxisOrigin();

  /**
   * Returns the meta object for enum '{@link info.limpet.stackedcharts.model.AxisScale <em>Axis Scale</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Axis Scale</em>'.
   * @see info.limpet.stackedcharts.model.AxisScale
   * @generated
   */
  EEnum getAxisScale();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  StackedchartsFactory getStackedchartsFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each operation of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.ChartSetImpl <em>Chart Set</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see info.limpet.stackedcharts.model.impl.ChartSetImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getChartSet()
     * @generated
     */
    EClass CHART_SET = eINSTANCE.getChartSet();

    /**
     * The meta object literal for the '<em><b>Charts</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CHART_SET__CHARTS = eINSTANCE.getChartSet_Charts();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.ChartImpl <em>Chart</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see info.limpet.stackedcharts.model.impl.ChartImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getChart()
     * @generated
     */
    EClass CHART = eINSTANCE.getChart();

    /**
     * The meta object literal for the '<em><b>Parent</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CHART__PARENT = eINSTANCE.getChart_Parent();

    /**
     * The meta object literal for the '<em><b>Axes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CHART__AXES = eINSTANCE.getChart_Axes();

    /**
     * The meta object literal for the '<em><b>Datasets</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CHART__DATASETS = eINSTANCE.getChart_Datasets();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CHART__NAME = eINSTANCE.getChart_Name();

    /**
     * The meta object literal for the '<em><b>Annotations</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CHART__ANNOTATIONS = eINSTANCE.getChart_Annotations();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.AxisImpl <em>Axis</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see info.limpet.stackedcharts.model.impl.AxisImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAxis()
     * @generated
     */
    EClass AXIS = eINSTANCE.getAxis();

    /**
     * The meta object literal for the '<em><b>Parent</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference AXIS__PARENT = eINSTANCE.getAxis_Parent();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute AXIS__NAME = eINSTANCE.getAxis_Name();

    /**
     * The meta object literal for the '<em><b>Origin</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute AXIS__ORIGIN = eINSTANCE.getAxis_Origin();

    /**
     * The meta object literal for the '<em><b>Scale</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute AXIS__SCALE = eINSTANCE.getAxis_Scale();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.DatasetImpl <em>Dataset</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see info.limpet.stackedcharts.model.impl.DatasetImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getDataset()
     * @generated
     */
    EClass DATASET = eINSTANCE.getDataset();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DATASET__NAME = eINSTANCE.getDataset_Name();

    /**
     * The meta object literal for the '<em><b>Axis</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DATASET__AXIS = eINSTANCE.getDataset_Axis();

    /**
     * The meta object literal for the '<em><b>Items</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DATASET__ITEMS = eINSTANCE.getDataset_Items();

    /**
     * The meta object literal for the '<em><b>Styling</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DATASET__STYLING = eINSTANCE.getDataset_Styling();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.DataItemImpl <em>Data Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see info.limpet.stackedcharts.model.impl.DataItemImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getDataItem()
     * @generated
     */
    EClass DATA_ITEM = eINSTANCE.getDataItem();

    /**
     * The meta object literal for the '<em><b>Independent Val</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DATA_ITEM__INDEPENDENT_VAL = eINSTANCE.getDataItem_IndependentVal();

    /**
     * The meta object literal for the '<em><b>Dependent Val</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DATA_ITEM__DEPENDENT_VAL = eINSTANCE.getDataItem_DependentVal();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.AnnotationImpl <em>Annotation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see info.limpet.stackedcharts.model.impl.AnnotationImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAnnotation()
     * @generated
     */
    EClass ANNOTATION = eINSTANCE.getAnnotation();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ANNOTATION__NAME = eINSTANCE.getAnnotation_Name();

    /**
     * The meta object literal for the '<em><b>Color</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ANNOTATION__COLOR = eINSTANCE.getAnnotation_Color();

    /**
     * The meta object literal for the '<em><b>Chart</b></em>' container reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ANNOTATION__CHART = eINSTANCE.getAnnotation_Chart();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.ZoneImpl <em>Zone</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see info.limpet.stackedcharts.model.impl.ZoneImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getZone()
     * @generated
     */
    EClass ZONE = eINSTANCE.getZone();

    /**
     * The meta object literal for the '<em><b>Start</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ZONE__START = eINSTANCE.getZone_Start();

    /**
     * The meta object literal for the '<em><b>End</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ZONE__END = eINSTANCE.getZone_End();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.MarkerImpl <em>Marker</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see info.limpet.stackedcharts.model.impl.MarkerImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getMarker()
     * @generated
     */
    EClass MARKER = eINSTANCE.getMarker();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MARKER__VALUE = eINSTANCE.getMarker_Value();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.StylingImpl <em>Styling</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see info.limpet.stackedcharts.model.impl.StylingImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getStyling()
     * @generated
     */
    EClass STYLING = eINSTANCE.getStyling();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.PlainStylingImpl <em>Plain Styling</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see info.limpet.stackedcharts.model.impl.PlainStylingImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getPlainStyling()
     * @generated
     */
    EClass PLAIN_STYLING = eINSTANCE.getPlainStyling();

    /**
     * The meta object literal for the '<em><b>Color</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PLAIN_STYLING__COLOR = eINSTANCE.getPlainStyling_Color();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.impl.LinearStylingImpl <em>Linear Styling</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see info.limpet.stackedcharts.model.impl.LinearStylingImpl
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getLinearStyling()
     * @generated
     */
    EClass LINEAR_STYLING = eINSTANCE.getLinearStyling();

    /**
     * The meta object literal for the '<em><b>Start Color</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute LINEAR_STYLING__START_COLOR = eINSTANCE.getLinearStyling_StartColor();

    /**
     * The meta object literal for the '<em><b>End Color</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute LINEAR_STYLING__END_COLOR = eINSTANCE.getLinearStyling_EndColor();

    /**
     * The meta object literal for the '<em><b>Start Val</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute LINEAR_STYLING__START_VAL = eINSTANCE.getLinearStyling_StartVal();

    /**
     * The meta object literal for the '<em><b>End Val</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute LINEAR_STYLING__END_VAL = eINSTANCE.getLinearStyling_EndVal();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.AxisOrigin <em>Axis Origin</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see info.limpet.stackedcharts.model.AxisOrigin
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAxisOrigin()
     * @generated
     */
    EEnum AXIS_ORIGIN = eINSTANCE.getAxisOrigin();

    /**
     * The meta object literal for the '{@link info.limpet.stackedcharts.model.AxisScale <em>Axis Scale</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see info.limpet.stackedcharts.model.AxisScale
     * @see info.limpet.stackedcharts.model.impl.StackedchartsPackageImpl#getAxisScale()
     * @generated
     */
    EEnum AXIS_SCALE = eINSTANCE.getAxisScale();

  }

} //StackedchartsPackage
