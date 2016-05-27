package info.limpet.stackedcharts.ui.editor.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.model.Styling;
import info.limpet.stackedcharts.ui.editor.commands.DeleteDatasetsFromAxisCommand;
import info.limpet.stackedcharts.ui.editor.figures.DatasetFigure;

public class DatasetEditPart extends AbstractGraphicalEditPart implements
    ActionListener
{

  private DatasetFigure contentPane;

  private DatasetAdapter adapter = new DatasetAdapter();

  @Override
  protected IFigure createFigure()
  {
    RectangleFigure figure = new RectangleFigure();
    figure.setOutline(false);
    FlowLayout layout = new FlowLayout(false);
    layout.setMinorAlignment(FlowLayout.ALIGN_CENTER);
    figure.setLayoutManager(layout);

    Button button = new Button("X");
    button.setToolTip(new Label("Remote the dataset from this axis"));
    button.addActionListener(this);
    figure.add(button);

    contentPane = new DatasetFigure();
    figure.add(contentPane);

    return figure;
  }

  @Override
  public IFigure getContentPane()
  {
    return contentPane;
  }

  @Override
  protected void createEditPolicies()
  {
    installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE,
        new NonResizableEditPolicy());
    installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy()
    {
      protected Command createDeleteCommand(GroupRequest deleteRequest)
      {
        Dataset dataset = (Dataset) getHost().getModel();
        DependentAxis parent = (DependentAxis) getHost().getParent().getModel();
        DeleteDatasetsFromAxisCommand cmd = new DeleteDatasetsFromAxisCommand(
            parent, dataset);
        return cmd;
      }
    });
  }

  @Override
  public void activate()
  {
    super.activate();
    getDataset().eAdapters().add(adapter);
  }

  @Override
  public void deactivate()
  {
    getDataset().eAdapters().remove(adapter);
    super.deactivate();
  }

  @Override
  protected void refreshVisuals()
  {
    contentPane.setName("Dataset: " + getDataset().getName());
  }

  protected Dataset getDataset()
  {
    return (Dataset) getModel();
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

  @Override
  protected List getModelChildren()
  {
    List<Styling> result = new ArrayList<>();
    Styling styling = getDataset().getStyling();
    if (styling != null)
    {
      result.add(styling);
    }
    return result;
  }

  public class DatasetAdapter implements Adapter
  {

    @Override
    public void notifyChanged(Notification notification)
    {
      int featureId = notification.getFeatureID(StackedchartsPackage.class);
      switch (featureId)
      {
      case StackedchartsPackage.DATASET__STYLING:
        refreshChildren();
        break;
      case StackedchartsPackage.DATASET__NAME:
        refreshVisuals();
        break;
      }
    }

    @Override
    public Notifier getTarget()
    {
      return getDataset();
    }

    @Override
    public void setTarget(Notifier newTarget)
    {
    }

    @Override
    public boolean isAdapterForType(Object type)
    {
      return type.equals(Dataset.class);
    }
  }
}
