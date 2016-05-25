package info.limpet.stackedcharts.ui.editor.parts;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.ui.editor.figures.ArrowFigure;

/**
 * Represents the shared (independent) axis of a {@link ChartSet} object
 */
public class SharedAxisEditPart extends AbstractGraphicalEditPart
{

  private static volatile Font boldFont;

  private AxisAdapter adapter = new AxisAdapter();

  private Label axisNameLabel;

  @Override
  public void activate()
  {
    super.activate();
    getAxis().eAdapters().add(adapter);
  }

  @Override
  public void deactivate()
  {
    getAxis().eAdapters().remove(adapter);
    super.deactivate();
  }

  protected IndependentAxis getAxis()
  {
    return (IndependentAxis) getModel();
  }

  @Override
  protected IFigure createFigure()
  {
    RectangleFigure rectangle = new RectangleFigure();
    rectangle.setOutline(false);
    GridLayout gridLayout = new GridLayout();
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    rectangle.setLayoutManager(gridLayout);

    ArrowFigure arrowFigure = new ArrowFigure(true);
    gridLayout.setConstraint(arrowFigure, new GridData(GridData.FILL,
        GridData.FILL, true, false));
    rectangle.add(arrowFigure);

    axisNameLabel = new Label();
    gridLayout.setConstraint(axisNameLabel, new GridData(GridData.CENTER,
        GridData.BEGINNING, false, false));
    rectangle.add(axisNameLabel);

    return rectangle;
  }

  @Override
  protected void refreshVisuals()
  {
    String name = getAxis().getName();
    if (name == null)
    {
      name = "<unnamed>";
    }
    axisNameLabel.setText("Shared axis: " + name);

    if (boldFont == null)
    {
      FontData fontData = axisNameLabel.getFont().getFontData()[0];
      boldFont = new Font(Display.getCurrent(), new FontData(fontData.getName(),
          fontData.getHeight(), SWT.BOLD));
    }
    axisNameLabel.setFont(boldFont);

    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = SWT.FILL;
    gridData.verticalAlignment = SWT.FILL;

    ((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
        gridData);
  }

  @Override
  protected void createEditPolicies()
  {
    installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE,
        new NonResizableEditPolicy());
  }

  public class AxisAdapter implements Adapter
  {

    @Override
    public void notifyChanged(Notification notification)
    {
      int featureId = notification.getFeatureID(StackedchartsPackage.class);
      switch (featureId)
      {
      case StackedchartsPackage.INDEPENDENT_AXIS__NAME:
        refreshVisuals();
      }
    }

    @Override
    public Notifier getTarget()
    {
      return getAxis();
    }

    @Override
    public void setTarget(Notifier newTarget)
    {
    }

    @Override
    public boolean isAdapterForType(Object type)
    {
      return type.equals(IndependentAxis.class);
    }
  }

}
