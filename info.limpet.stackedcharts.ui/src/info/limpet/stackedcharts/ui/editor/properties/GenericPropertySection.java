package info.limpet.stackedcharts.ui.editor.properties;

import info.limpet.stackedcharts.ui.editor.commands.ChartCommand;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EDataTypeImpl;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

abstract public class GenericPropertySection<Element extends EObject> extends
    AbstractPropertySection
{

  protected Element element;
  private List<CombinedProperty> myProps;

  @Override
  public void createControls(Composite parent,
      TabbedPropertySheetPage aTabbedPropertySheetPage)
  {
    super.createControls(parent, aTabbedPropertySheetPage);

    parent.setLayout(new GridLayout(2, false));

    TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
    createMyProperties(parent, factory);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setInput(IWorkbenchPart part, ISelection selection)
  {
    super.setInput(part, selection);

    if (selection instanceof StructuredSelection)
    {
      Object selected = ((StructuredSelection) selection).getFirstElement();
      if (selected instanceof EditPart)
      {
        selected = ((EditPart) selected).getModel();
        if (selected instanceof EObject)
        {
          element = (Element) selected;
          // ideally we should also attach listeners here to the chart, so that if name changes,
          // "refresh" would be invoked
        }
      }

    }
  }

  /** get our specific list of properties
   * 
   * @return
   */
  abstract protected List<CombinedProperty> getProperties();

  /** generate the properties, plus their editors
   * 
   * @param parent
   * @param factory
   */
  protected final void createMyProperties(Composite parent,
      TabbedPropertySheetWidgetFactory factory)
  {
    // retrieve the properties
    myProps = getProperties();

    // now create them
    for (final CombinedProperty thisProp : myProps)
    {
      // create the label
      factory.createLabel(parent, thisProp.name + ":");

      // and the control
      final Control control = thisProp.create(factory, parent);
      
      // and connect it
      control.addFocusListener(new FocusAdapter()
      {
        @Override
        public void focusLost(FocusEvent e)
        {
          if (thisProp.isModified(element))// !thisText.getText().equals(element.getName()))
          {
            CommandStack commandStack =
                (CommandStack) getPart().getAdapter(CommandStack.class);

            commandStack.execute(new ChartCommand(element, thisProp.attribute,
                thisProp.getValue()));
          }
        }
      });
      GridDataFactory.swtDefaults().hint(150, SWT.DEFAULT).applyTo(control);
    }
  }

  @Override
  public final void refresh()
  {
    // now create them
    for (CombinedProperty thisProp : myProps)
    {
      // refresh this property
      thisProp.refresh(element);
    }
  }

  /** helper class that stores everything related to a property
   * 
   * @author ian
   *
   */
  protected static class CombinedProperty
  {
    /** the human-readable name for this property
     * 
     */
    private final String name;
    
    /** the attribute that is being edited
     * 
     */
    protected final EAttribute attribute;
    
    /** a UI-control-specific helper object
     * 
     */
    protected ControlHelper controlHelper;

    protected CombinedProperty(EAttribute attribute)
    {
      this(attribute.getName(), attribute);
    }
    
    protected CombinedProperty(String name, EAttribute attribute)
    {
      this.name = name;
      this.attribute = attribute;

      // sort out the correct type of helper
      EClassifier eType = attribute.getEType();
      if (eType instanceof EEnum)
      {
        controlHelper = new ComboHelper((EEnum) eType);
      }
      else if(eType instanceof EDataTypeImpl)
      {
        // check the data type
        if(eType.getName().equals("EString"))
        {
          controlHelper = new TextHelper();
        }
        else if(eType.getName().equals("Boolean"))
        {
          controlHelper = new BooleanHelper();
        }
      }
      
      if(controlHelper == null)
      {
        System.err.println("NOTE: failed to create helper for:" + name);
      }
      
    }

    /** create our control
     * 
     * @param factory
     * @param parent
     * @return
     */
    public Control create(TabbedPropertySheetWidgetFactory factory,
        Composite parent)
    {
      return controlHelper.create(factory, parent);
    }

    /** refresh the control
     * @param subject 
     * 
     */
    public void refresh(EObject subject)
    {
      controlHelper.refresh(subject, attribute);
    }

    /** has the control been modified?
     * @param subject 
     * 
     * @return
     */
    public boolean isModified(EObject subject)
    {
      return controlHelper.isModified(subject, attribute);
    }

    /** get the current value in the control
     * 
     * @return
     */
    public Object getValue()
    {
      return controlHelper.getValue();
    }
  }

  /** wrapper for an SWT UI control
   * 
   * @author ian
   *
   */
  private static interface ControlHelper
  {
    /** refresh the value displayed by the control
     * 
     * @param subject
     * @param attribute
     */
    void refresh(EObject subject, EAttribute attribute);

    /** has the control been modified?
     * 
     * @param subject
     * @param attribute
     * @return
     */
    boolean isModified(EObject subject, EAttribute attribute);

    /** return the control value (in data units)
     * 
     * @return
     */
    Object getValue();

    /** create our editor control
     * 
     * @param factory
     * @param parent
     * @return
     */
    Control create(TabbedPropertySheetWidgetFactory factory, Composite parent);
  }

  /** a text editor
   * 
   * @author ian
   *
   */
  private static class TextHelper implements ControlHelper
  {

    private Text control;

    @Override
    public void refresh(EObject subject, EAttribute attribute)
    {
      
      String curVal = (String) subject.eGet(attribute);
      if(curVal == null)
      {
        curVal = "";
      }
      control.setText(curVal);
    }

    @Override
    public Control create(TabbedPropertySheetWidgetFactory factory,
        Composite parent)
    {
      control = factory.createText(parent, null);
      return control;
    }

    @Override
    public boolean isModified(EObject subject, EAttribute attribute)
    {
      return !control.getText().equals(subject.eGet(attribute));
    }

    @Override
    public Object getValue()
    {
      return control.getText();
    }
  }
  

  /** a boolean editor
   * 
   * @author ian
   *
   */
  private static class BooleanHelper implements ControlHelper
  {

    // TODO: we do need a proper checkbox editor in here
    private Text control;

    @Override
    public void refresh(EObject subject, EAttribute attribute)
    {
      Boolean value = (Boolean) subject.eGet(attribute);
      final String res;
      if(value)
        res = "True";
      else
        res = "False";
      control.setText(res);
    }

    @Override
    public Control create(TabbedPropertySheetWidgetFactory factory,
        Composite parent)
    {
      control = factory.createText(parent, null);
      return control;
    }

    @Override
    public boolean isModified(EObject subject, EAttribute attribute)
    {
      return !control.getText().equals(subject.eGet(attribute));
    }

    @Override
    public Object getValue()
    {
      return Boolean.parseBoolean(control.getText());
    }
  }

  /** a combo editor - for enumerated data items
   * 
   * @author ian
   *
   */
  private static class ComboHelper implements ControlHelper
  {
    private CCombo control;
    private final EEnum enumType;

    public ComboHelper(EEnum enumType)
    {
      this.enumType = enumType;
    }

    @Override
    public void refresh(EObject subject, EAttribute attribute)
    {
      final Enumerator thisVal = (Enumerator) subject.eGet(attribute);
      EList<EEnumLiteral> literals = enumType.getELiterals();
      for (int i = 0; i < literals.size(); i++)
      {
        final EEnumLiteral thisE = literals.get(i);
        if (thisE.getLiteral().equals(thisVal.getLiteral()))
        {
          control.select(i);
          return;
        }
      }
    }

    @Override
    public Control create(TabbedPropertySheetWidgetFactory factory,
        Composite parent)
    {
      control = factory.createCCombo(parent);

      EList<EEnumLiteral> literals = enumType.getELiterals();
      String[] lits = new String[literals.size()];
      int ctr = 0;
      for (EEnumLiteral thisE : literals)
      {
        lits[ctr++] = thisE.getLiteral();
      }
      control.setItems(lits);

      return control;
    }

    @Override
    public Object getValue()
    {
      final int value = control.getSelectionIndex();
      return enumType.getELiterals().get(value);
    }

    @Override
    public boolean isModified(EObject subject, EAttribute attribute)
    {
      final EEnumLiteral editorVal = (EEnumLiteral) getValue();
      return !editorVal.equals(subject.eGet(attribute));
    }
  }
}