package info.limpet.stackedcharts.ui.editor.parts;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.ui.editor.commands.DeleteAxisFromChartCommand;
import info.limpet.stackedcharts.ui.editor.figures.ArrowFigure;
import info.limpet.stackedcharts.ui.editor.figures.VerticalLabel;
import info.limpet.stackedcharts.ui.editor.policies.AxisContainerEditPolicy;

import java.util.List;

import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.FlowLayout;
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
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class AxisEditPart extends AbstractGraphicalEditPart implements ActionListener
{

  public static final Color BACKGROUND_COLOR = Display.getDefault()
      .getSystemColor(SWT.COLOR_WHITE);

  private RectangleFigure datasetsPane;

  private VerticalLabel axisNameLabel;

  private AxisAdapter adapter = new AxisAdapter();

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

  protected DependentAxis getAxis()
  {
    return (DependentAxis) getModel();
  }

  @Override
  protected IFigure createFigure()
  {
    RectangleFigure figure = new RectangleFigure();
    figure.setBackgroundColor(BACKGROUND_COLOR);

    figure.setOutline(false);
    GridLayout layoutManager = new GridLayout(4, false);
    // zero margin, in order to connect the dependent axes to the shared one
    layoutManager.marginHeight = 0;
    layoutManager.marginWidth = 0;
    figure.setLayoutManager(layoutManager);

    datasetsPane = new RectangleFigure();
    datasetsPane.setOutline(false);
    FlowLayout datasetsPaneLayout = new FlowLayout();
    datasetsPaneLayout.setHorizontal(true);
    datasetsPaneLayout.setStretchMinorAxis(true);
    datasetsPane.setLayoutManager(datasetsPaneLayout);
    figure.add(datasetsPane);

    ArrowFigure arrowFigure = new ArrowFigure();
    layoutManager.setConstraint(arrowFigure, new GridData(GridData.FILL,
        GridData.FILL, false, true));
    figure.add(arrowFigure);

    axisNameLabel = new VerticalLabel();
    layoutManager.setConstraint(axisNameLabel, new GridData(GridData.FILL,
        GridData.FILL, true, true));
    figure.add(axisNameLabel);
    
    Button button = new Button("X");
    layoutManager.setConstraint(button, new GridData(GridData.FILL,
        GridData.FILL, true, true));
    button.setToolTip(new Label("Remove the axis from this chart"));
    button.addActionListener(this);
    figure.add(button);

    return figure;
  }

  @Override
  protected void refreshVisuals()
  {
    axisNameLabel.setText(getAxis().getName());

    ((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
        new GridData(GridData.CENTER, GridData.FILL, false, true));
  }

  @Override
  public IFigure getContentPane()
  {
    return datasetsPane;
  }

  @Override
  protected void createEditPolicies()
  {
    installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE,
        new NonResizableEditPolicy());

    installEditPolicy(EditPolicy.CONTAINER_ROLE, new AxisContainerEditPolicy());
    
    installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy()
    {
      protected Command createDeleteCommand(GroupRequest deleteRequest)
      {
        DependentAxis dataset = (DependentAxis) getHost().getModel();
        Chart parent = (Chart) dataset.eContainer();
        DeleteAxisFromChartCommand cmd =
            new DeleteAxisFromChartCommand(parent, dataset);
        return cmd;
      }
    });
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected List getModelChildren()
  {
    return getAxis().getDatasets();
  }

  public class AxisAdapter implements Adapter
  {

    @Override
    public void notifyChanged(Notification notification)
    {
      int featureId = notification.getFeatureID(StackedchartsPackage.class);
      switch (featureId)
      {
      case StackedchartsPackage.DEPENDENT_AXIS__DATASETS:
        refreshChildren();
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
      return type.equals(DependentAxis.class);
    }
  }

  @Override
  public void actionPerformed(org.eclipse.draw2d.ActionEvent event)
  {
    Command deleteCommand = getCommand(new GroupRequest(REQ_DELETE));
    if (deleteCommand != null)
    {
      CommandStack commandStack = getViewer().getEditDomain().getCommandStack();
      commandStack.execute(deleteCommand);
    }
  }

}
