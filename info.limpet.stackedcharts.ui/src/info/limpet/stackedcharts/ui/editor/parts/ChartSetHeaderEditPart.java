package info.limpet.stackedcharts.ui.editor.parts;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.ui.editor.commands.AddChartCommand;
import info.limpet.stackedcharts.ui.editor.figures.ChartsetFigure;

import java.util.List;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.SWT;

/**
 * Represents header of a {@link ChartSet} object
 */
public class ChartSetHeaderEditPart extends AbstractGraphicalEditPart implements
    ActionListener
{

  @Override
  protected IFigure createFigure()
  {

    return new ChartsetFigure(this);
  }

  @Override
  protected void refreshVisuals()
  {
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = false;
    gridData.horizontalAlignment = SWT.CENTER;
    gridData.horizontalSpan = 10;
    gridData.verticalAlignment = SWT.FILL;

    ((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
        gridData);
  }

  @Override
  protected void createEditPolicies()
  {

  }

  @Override
  public ChartSet getModel()
  {
    return ((ChartSetEditPart.ChartSetWrapper) super.getModel()).getcChartSet();
  }

  @Override
  public void actionPerformed(ActionEvent event)
  {
    List<Chart> charts = getModel().getCharts();
    StackedchartsFactory factory = StackedchartsFactory.eINSTANCE;
    Chart chart = factory.createChart();
    chart.setName("New Chart");
    AddChartCommand addChartCommand = new AddChartCommand(charts, chart);
    CommandStack commandStack = getViewer().getEditDomain().getCommandStack();
    commandStack.execute(addChartCommand);

  }

}
