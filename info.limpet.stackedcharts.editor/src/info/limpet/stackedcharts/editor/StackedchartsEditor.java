package info.limpet.stackedcharts.editor;

import info.limpet.stackedcharts.editor.parts.StackedChartsEditPartFactory;
import info.limpet.stackedcharts.model.ChartSet;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;

public class StackedchartsEditor extends GraphicalEditorWithFlyoutPalette
{

  private Resource emfResource;
  private ChartSet chartSet;

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
    // TODO Auto-generated method stub

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
  protected void initializeGraphicalViewer()
  {
    super.initializeGraphicalViewer();
    getGraphicalViewer().setContents(chartSet);
  }

  @Override
  protected void configureGraphicalViewer()
  {
    super.configureGraphicalViewer();
    getGraphicalViewer().setEditPartFactory(new StackedChartsEditPartFactory());
  }
}
