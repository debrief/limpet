package info.limpet.stackedcharts.ui.editor;

import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.ui.editor.parts.StackedChartsEditPartFactory;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class StackedchartsEditControl extends Composite
{

  private final EditDomain editDomain;
  private final GraphicalViewer viewer;

  public StackedchartsEditControl(Composite parent)
  {
    super(parent, SWT.NONE);

    setLayout(new FillLayout());

    editDomain = new EditDomain();

    viewer = new ScrollingGraphicalViewer();
    viewer.createControl(this);
    editDomain.addViewer(viewer);

    viewer.getControl().setBackground(ColorConstants.listBackground);

    viewer.setEditPartFactory(new StackedChartsEditPartFactory());

  }

  public GraphicalViewer getViewer()
  {
    return viewer;
  }

  public void setModel(ChartSet model)
  {
    viewer.setContents(model);
  }
}
