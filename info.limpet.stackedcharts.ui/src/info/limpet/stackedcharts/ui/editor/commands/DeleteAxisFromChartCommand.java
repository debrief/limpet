package info.limpet.stackedcharts.ui.editor.commands;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.DependentAxis;

import org.eclipse.gef.commands.Command;

public class DeleteAxisFromChartCommand extends Command
{
  private final DependentAxis[] datasets;
  private final Chart parent;

  public DeleteAxisFromChartCommand(Chart parent, DependentAxis... datasets)
  {
    this.datasets = datasets;
    this.parent = parent;
  }

  @Override
  public void execute()
  {
    for (DependentAxis ds : datasets)
    {
      parent.getMinAxes().remove(ds);
      parent.getMaxAxes().remove(ds);
    }
  }

  @Override
  public void undo()
  {
    // TODO: support undo
    throw new RuntimeException("Method not implemented");
  }
}
