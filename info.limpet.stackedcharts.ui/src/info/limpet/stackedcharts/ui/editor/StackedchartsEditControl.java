package info.limpet.stackedcharts.ui.editor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory.Descriptor.Registry;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.view.ExtendedPropertySheetPage;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.gef.ui.actions.RedoAction;
import org.eclipse.gef.ui.actions.UndoAction;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertySheetPage;

import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.ui.editor.drop.DatasetToAxisDropTargetListener;
import info.limpet.stackedcharts.ui.editor.drop.DatasetToAxisLandingDropTargetListener;
import info.limpet.stackedcharts.ui.editor.drop.DatasetToChartDropTargetListener;
import info.limpet.stackedcharts.ui.editor.drop.ProxyDropTargetListener;
import info.limpet.stackedcharts.ui.editor.drop.ScatterSetToScatterSetContainerTargetListener;
import info.limpet.stackedcharts.ui.editor.parts.IPropertySourceProvider;
import info.limpet.stackedcharts.ui.editor.parts.StackedChartsEditPartFactory;
import info.limpet.stackedcharts.ui.emfgefstack.EmfGefCommandStack;

public class StackedchartsEditControl extends Composite
{

  private class CustomComposedAdapterFactory extends ComposedAdapterFactory
      implements IEditingDomainProvider
  {

    private EditingDomain editingDomain;

    public CustomComposedAdapterFactory(Registry instance)
    {
      super(instance);
    }

    @Override
    public EditingDomain getEditingDomain()
    {
      return editingDomain;
    }

    public void setEditingDomain(EditingDomain editingDomain)
    {
      this.editingDomain = editingDomain;
    }

  }

  private final GraphicalViewer viewer;

  protected AdapterFactoryEditingDomain emfEditingDomain;
  private CustomComposedAdapterFactory adapterFactory;
  private ExtendedPropertySheetPage propertySheetPage;

  public StackedchartsEditControl(Composite parent)
  {
    super(parent, SWT.NONE);

    setLayout(new FillLayout());

    EditDomain editDomain = new EditDomain();

    EmfGefCommandStack commandStack = new EmfGefCommandStack();
    editDomain.setCommandStack(commandStack.getWrappedGefCommandStack());

    viewer = new ScrollingGraphicalViewer();

    // connect external Drop support
    // add Dataset to Axis

    viewer.addDropTargetListener(new ProxyDropTargetListener(
        new DatasetToAxisDropTargetListener(viewer),
        new DatasetToAxisLandingDropTargetListener(viewer),
        new ScatterSetToScatterSetContainerTargetListener(viewer),
        new DatasetToChartDropTargetListener(viewer)));

    viewer.createControl(this);
    editDomain.addViewer(viewer);

    viewer.getControl().setBackground(ColorConstants.listBackground);

    viewer.setEditPartFactory(new StackedChartsEditPartFactory());

    // emf edit domain
    adapterFactory = new CustomComposedAdapterFactory(
        ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
    adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
    adapterFactory.addAdapterFactory(new EcoreItemProviderAdapterFactory());
    adapterFactory.addAdapterFactory(
        new ReflectiveItemProviderAdapterFactory());
    emfEditingDomain = new AdapterFactoryEditingDomain(adapterFactory,
        commandStack);

    adapterFactory.setEditingDomain(emfEditingDomain);    
  }

  /**
   * This accesses a cached version of the property sheet.
   */
  public IPropertySheetPage getPropertySheetPage()
  {
    if (propertySheetPage == null)
    {
      propertySheetPage = new ExtendedPropertySheetPage(emfEditingDomain)
      {
        @Override
        public void setSelectionToViewer(List<?> selection)
        {
          StackedchartsEditControl.this.setSelectionToViewer(selection);
        }
      };
      propertySheetPage.setPropertySourceProvider(
          new AdapterFactoryContentProvider(adapterFactory));

    }

    return propertySheetPage;
  }

  /**
   * This method should return a {@link org.eclipse.jface.viewers.StructuredSelection} containing
   * one or more of the viewer's EditParts underline EMF objects . If no editparts are selected,
   * root EMF object will return
   * 
   */
  public ISelection getSelection()
  {
    StructuredSelection selection = (StructuredSelection) viewer.getSelection();
    if (!selection.isEmpty())
    {
      List<Object> emfObj = new ArrayList<Object>();
      Object[] array = selection.toArray();
      for (Object object : array)
      {
        if (object instanceof AbstractEditPart)
        {
          AbstractEditPart abstractEditPart = (AbstractEditPart) object;
          if (abstractEditPart instanceof IPropertySourceProvider)
          {
            IPropertySourceProvider mergeModelProvider =
                (IPropertySourceProvider) abstractEditPart;
            IPropertySource merged = mergeModelProvider.getPropertySource();
            if (merged == null)
            {
              emfObj.add(abstractEditPart.getModel());
            }
            else
            {
              emfObj.add(merged);
            }
          }
          else
            emfObj.add(abstractEditPart.getModel());
        }
      }
      return new StructuredSelection(emfObj);
    }

    return viewer.getSelection();
  }

  private void setSelectionToViewer(Collection<?> collection)
  {
    final Collection<?> theSelection = collection;

    if (theSelection != null && !theSelection.isEmpty())
    {
      Runnable runnable = new Runnable()
      {
        public void run()
        {

          if (viewer != null)
          {
            viewer.setSelection(new StructuredSelection(theSelection
                .toArray()));
          }
        }
      };
      Display.getCurrent().asyncExec(runnable);
    }
  }

  public GraphicalViewer getViewer()
  {
    return viewer;
  }

  public void setModel(ChartSet model)
  {
    viewer.setContents(model);

  }

  public void init(IViewPart view)
  {
    IActionBars actionBars = view.getViewSite().getActionBars();
    IToolBarManager toolBarManager = actionBars.getToolBarManager();

    final UndoAction undoAction = new UndoAction(view);
    toolBarManager.add(undoAction);
    undoAction.setImageDescriptor(Activator.imageDescriptorFromPlugin(
        Activator.PLUGIN_ID, "icons/undo.png"));
    final RedoAction redoAction = new RedoAction(view);
    redoAction.setImageDescriptor(Activator.imageDescriptorFromPlugin(
        Activator.PLUGIN_ID, "icons/redo.png"));
    toolBarManager.add(redoAction);

    emfEditingDomain.getCommandStack().addCommandStackListener(
        new CommandStackListener()
        {

          @Override
          public void commandStackChanged(EventObject event)
          {
            undoAction.setEnabled(undoAction.isEnabled());
            redoAction.setEnabled(redoAction.isEnabled());
          }
        });

  }
}
