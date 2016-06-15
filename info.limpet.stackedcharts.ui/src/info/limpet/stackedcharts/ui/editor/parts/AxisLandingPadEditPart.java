package info.limpet.stackedcharts.ui.editor.parts;

import info.limpet.stackedcharts.ui.editor.figures.DirectionalLabel;
import info.limpet.stackedcharts.ui.editor.parts.ChartEditPart.ChartPanePosition;
import info.limpet.stackedcharts.ui.editor.policies.AxisLandingPadEditPolicy;

import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class AxisLandingPadEditPart extends AbstractGraphicalEditPart
{

  @Override
  protected void createEditPolicies()
  {
    installEditPolicy(EditPolicy.CONTAINER_ROLE, new AxisLandingPadEditPolicy());
  }

  @Override
  protected IFigure createFigure()
  {
    final RectangleFigure figure = new RectangleFigure();
    figure.setOutline(false);
    final Color borderCol = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
    final Border figureBorder = new LineBorder(borderCol, 2);
    figure.setBorder(figureBorder);

    figure.setLayoutManager(new BorderLayout());
    final DirectionalLabel verticalLabel = new DirectionalLabel();

    final ChartPaneEditPart.AxisLandingPad pad =
        (ChartPaneEditPart.AxisLandingPad) getModel();

    verticalLabel.setText(pad.pos == ChartPanePosition.MIN ? "Min Axis"
        : "Max Axis");
    figure.setPreferredSize(30, 80);

    figure.add(verticalLabel, BorderLayout.CENTER);

    return figure;
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected List getModelChildren()
  {
    return Arrays.asList();
  }

  @Override
  protected void refreshVisuals()
  {
    ((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
        new GridData(GridData.CENTER, GridData.FILL, false, true));
  }
}
