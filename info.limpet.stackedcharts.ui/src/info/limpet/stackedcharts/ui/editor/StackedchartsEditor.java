package info.limpet.stackedcharts.ui.editor;

import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.ui.editor.parts.StackedChartsEditPartFactory;

import java.io.IOException;
import java.util.EventObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class StackedchartsEditor extends GraphicalEditorWithFlyoutPalette
    implements ITabbedPropertySheetPageContributor
{

  private Resource emfResource;
  private ChartSet chartSet;
  private TabbedPropertySheetPage propertySheetPage;

  public StackedchartsEditor()
  {
    setEditDomain(new DefaultEditDomain(this));
  }

  @Override
  protected PaletteRoot getPaletteRoot()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void doSave(IProgressMonitor monitor)
  {
    if (emfResource == null)
    {
      return;
    }

    try
    {
      emfResource.save(null);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    getCommandStack().markSaveLocation();
  }

  @Override
  public void init(IEditorSite site, IEditorInput input)
      throws PartInitException
  {
    super.init(site, input);

    if (input instanceof IFileEditorInput)
    {
      IFile file = ((IFileEditorInput) input).getFile();
      emfResource =
          new ResourceSetImpl().createResource(URI.createURI(file
              .getLocationURI().toString()));
      try
      {
        emfResource.load(null);
        chartSet = (ChartSet) emfResource.getContents().get(0);
      }
      catch (IOException e)
      {
        e.printStackTrace();
        emfResource = null;
      }
    }
  }

  @Override
  public void commandStackChanged(EventObject event)
  {
    firePropertyChange(PROP_DIRTY);
    super.commandStackChanged(event);
  }

  @Override
  protected void initializeGraphicalViewer()
  {
    super.initializeGraphicalViewer();
    getGraphicalViewer().setContents(chartSet);

    propertySheetPage = new TabbedPropertySheetPage(this);
  }

  @Override
  protected void configureGraphicalViewer()
  {
    super.configureGraphicalViewer();
    getGraphicalViewer().setEditPartFactory(new StackedChartsEditPartFactory());
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Object getAdapter(Class type)
  {
    if (type == IPropertySheetPage.class)
    {
      return propertySheetPage;
    }
    else if (type == CommandStack.class)
    {
      return getEditDomain().getCommandStack();
    }
    return super.getAdapter(type);
  }

  @Override
  public String getContributorId()
  {
    return getSite().getId();
  }
}
