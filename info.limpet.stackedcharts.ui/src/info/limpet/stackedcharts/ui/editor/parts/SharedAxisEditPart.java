package info.limpet.stackedcharts.ui.editor.parts;

import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.ui.editor.StackedchartsImages;
import info.limpet.stackedcharts.ui.editor.figures.ArrowFigure;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

/**
 * Represents the shared (independent) axis of a {@link ChartSet} object
 */
public class SharedAxisEditPart extends AbstractGraphicalEditPart
{

  private static volatile Font boldFont;

  private AxisAdapter adapter = new AxisAdapter();

  private ChartSetAdapter chartSetAdapter = new ChartSetAdapter();

  private Label axisNameLabel;

  private ArrowFigure arrowFigure;

  @Override
  public void activate()
  {
    super.activate();
    getAxis().eAdapters().add(adapter);
    chartSetAdapter.attachTo((ChartSet) getParent().getModel());
  }

  @Override
  public void deactivate()
  {
    getAxis().eAdapters().remove(adapter);
    chartSetAdapter.attachTo(null);
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

    arrowFigure = new ArrowFigure(true);
    rectangle.add(arrowFigure);

    // create holder for the name and the label
    RectangleFigure second_row = new RectangleFigure();
    second_row.setOutline(false);
    gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    gridLayout.marginHeight = 0;
    gridLayout.marginWidth = 0;
    second_row.setLayoutManager(gridLayout);

    rectangle.add(second_row);

    // ok, now add the image
    Label image =
        new Label(StackedchartsImages.getImage(StackedchartsImages.DESC_AXIS));
    gridLayout.setConstraint(image, new GridData(GridData.CENTER,
        GridData.CENTER, false, false));
    second_row.add(image);

    // and the text label
    axisNameLabel = new Label();

    gridLayout.setConstraint(axisNameLabel, new GridData(GridData.CENTER,
        GridData.BEGINNING, true, false));
    second_row.add(axisNameLabel);

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
      boldFont =
          new Font(Display.getCurrent(), new FontData(fontData.getName(),
              fontData.getHeight(), SWT.BOLD));
    }
    axisNameLabel.setFont(boldFont);

    GridData gridData = new GridData();
    gridData.horizontalAlignment = SWT.FILL;
    gridData.verticalAlignment = SWT.FILL;

    EditPart parent = getParent();
    ((GraphicalEditPart) parent).setLayoutConstraint(this, figure, gridData);

    boolean horizontal =
        ((ChartSet) parent.getModel()).getOrientation() == Orientation.HORIZONTAL;
    arrowFigure.setHorizontal(!horizontal);

    LayoutManager layoutManager = getFigure().getLayoutManager();
    layoutManager.setConstraint(arrowFigure, new GridData(GridData.FILL,
        GridData.FILL, !horizontal, horizontal));

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

  public class ChartSetAdapter implements Adapter
  {

    private Notifier target;

    @Override
    public void notifyChanged(Notification notification)
    {
      int featureId = notification.getFeatureID(StackedchartsPackage.class);
      switch (featureId)
      {
      case StackedchartsPackage.CHART_SET__ORIENTATION:
        refreshVisuals();
      }
    }

    @Override
    public Notifier getTarget()
    {
      return target;
    }

    @Override
    public void setTarget(Notifier newTarget)
    {
      this.target = newTarget;
    }

    void attachTo(Notifier newTarget)
    {
      if (this.target != null)
      {
        this.target.eAdapters().remove(this);
      }
      setTarget(newTarget);
      if (this.target != null)
      {
        this.target.eAdapters().add(this);
      }
    }

    @Override
    public boolean isAdapterForType(Object type)
    {
      return type.equals(ChartSet.class);
    }
  }
}
