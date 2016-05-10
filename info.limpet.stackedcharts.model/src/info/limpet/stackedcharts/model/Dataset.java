/**
 */
package info.limpet.stackedcharts.model;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Dataset</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link info.limpet.stackedcharts.model.Dataset#getName <em>Name</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.Dataset#getAxis <em>Axis</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.Dataset#getItems <em>Items</em>}</li>
 *   <li>{@link info.limpet.stackedcharts.model.Dataset#getStyling <em>Styling</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDataset()
 * @model
 * @generated
 */
public interface Dataset extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDataset_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Dataset#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Axis</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Axis</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Axis</em>' reference.
   * @see #setAxis(Axis)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDataset_Axis()
   * @model required="true"
   * @generated
   */
  Axis getAxis();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Dataset#getAxis <em>Axis</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Axis</em>' reference.
   * @see #getAxis()
   * @generated
   */
  void setAxis(Axis value);

  /**
   * Returns the value of the '<em><b>Items</b></em>' containment reference list.
   * The list contents are of type {@link info.limpet.stackedcharts.model.DataItem}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Items</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Items</em>' containment reference list.
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDataset_Items()
   * @model containment="true"
   * @generated
   */
  EList<DataItem> getItems();

  /**
   * Returns the value of the '<em><b>Styling</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Styling</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Styling</em>' containment reference.
   * @see #setStyling(Styling)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDataset_Styling()
   * @model containment="true" required="true"
   * @generated
   */
  Styling getStyling();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Dataset#getStyling <em>Styling</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Styling</em>' containment reference.
   * @see #getStyling()
   * @generated
   */
  void setStyling(Styling value);

} // Dataset
