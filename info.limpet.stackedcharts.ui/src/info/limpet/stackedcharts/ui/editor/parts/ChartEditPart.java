package info.limpet.stackedcharts.ui.editor.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.gef.EditPart;
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

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.ui.editor.commands.DeleteChartCommand;
import info.limpet.stackedcharts.ui.editor.figures.ChartFigure;
import info.limpet.stackedcharts.ui.editor.policies.ChartContainerEditPolicy;

public class ChartEditPart extends AbstractGraphicalEditPart implements
    ActionListener
{
  public static final Color BACKGROUND_COLOR = Display.getDefault()
      .getSystemColor(SWT.COLOR_WHITE);

  /**
   * Helper class to handle the container of {@link ScatterSet}s
   */
  public static class ScatterSetContainer extends ArrayList<ScatterSet>
  {

  }

  public enum ChartPanePosition
  {
    MIN, MAX
  }

  private ChartAdapter adapter = new ChartAdapter();

  @Override
  public void activate()
  {
    super.activate();
    getModel().eAdapters().add(adapter);
  }

  @Override
  public void deactivate()
  {
    getModel().eAdapters().remove(adapter);
    super.deactivate();
  }

  @Override
  protected IFigure createFigure()
  {
    return new ChartFigure(getModel(), this);
  }

  @Override
  public Chart getModel()
  {
    return (Chart) super.getModel();
  }

  @Override
  protected void createEditPolicies()
  {
    installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE,
        new NonResizableEditPolicy());
    installEditPolicy(EditPolicy.CONTAINER_ROLE,
        new ChartContainerEditPolicy());

    installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy()
    {
      protected Command createDeleteCommand(GroupRequest deleteRequest)
      {
        Chart chart = getModel();
        ChartSet parent = chart.getParent();
        DeleteChartCommand deleteChartCommand = new DeleteChartCommand(parent,
            chart);
        return deleteChartCommand;
      }
    });
  }

  @Override
  protected List getModelChildren()
  {
    List modelChildren = new ArrayList();
    modelChildren.addAll(Arrays.asList(ChartPanePosition.values()));
    ScatterSetContainer scatterSets = new ScatterSetContainer();
    for (SelectiveAnnotation annotation : getModel().getParent().getSharedAxis()
        .getAnnotations())
    {
      if (annotation.getAnnotation() instanceof ScatterSet && annotation
          .getAppearsIn().contains(getModel()))
      {
        scatterSets.add((ScatterSet) annotation.getAnnotation());
      }
    }
    modelChildren.add(scatterSets);
    return modelChildren;
  }

  @Override
  protected void refreshVisuals()
  {
    String name = getModel().getName();
    ChartFigure chartFigure = (ChartFigure) getFigure();
    chartFigure.setName(name);
    chartFigure.setVertical(getModel().getParent()
        .getOrientation() == Orientation.VERTICAL);

    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = SWT.FILL;
    gridData.verticalAlignment = SWT.FILL;

    ((GraphicalEditPart) getParent()).setLayoutConstraint(this, figure,
        gridData);

  }

  protected void refreshChildren()
  {
    // remove all Childs
    @SuppressWarnings("unchecked")
    List<EditPart> children = getChildren();
    for (EditPart object : new ArrayList<EditPart>(children))
    {
      removeChild(object);
    }
    // add back all model elements
    List modelObjects = getModelChildren();
    for (int i = 0; i < modelObjects.size(); i++)
    {
      addChild(createChild(modelObjects.get(i)), i);

    }

    ((ChartFigure) getFigure()).getLayoutManager().layout(getFigure());
  }

  @Override
  public void actionPerformed(ActionEvent event)
  {
    Command deleteCommand = getCommand(new GroupRequest(REQ_DELETE));
    if (deleteCommand != null)
    {
      CommandStack commandStack = getViewer().getEditDomain().getCommandStack();
      commandStack.execute(deleteCommand);
    }

  }

  public class ChartAdapter implements Adapter
  {

    @Override
    public void notifyChanged(Notification notification)
    {
      int featureId = notification.getFeatureID(StackedchartsPackage.class);
      switch (featureId)
      {
      case StackedchartsPackage.CHART__NAME:
        refreshVisuals();
        break;
      case StackedchartsPackage.CHART__MAX_AXES:
        refreshChildren();
        break;
      case StackedchartsPackage.CHART__MIN_AXES:
        refreshChildren();
        break;
      }
    }

    @Override
    public Notifier getTarget()
    {
      return getModel();
    }

    @Override
    public void setTarget(Notifier newTarget)
    {
      // Do nothing.
    }

    @Override
    public boolean isAdapterForType(Object type)
    {
      return type.equals(Chart.class);
    }
  }

}
