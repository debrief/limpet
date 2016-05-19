package info.limpet.stackedcharts.ui.editor.commands;

import org.eclipse.gef.commands.Command;

import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;

public class DeleteDatasetFromAxisCommand extends Command
{
  private final Dataset dataset;
  private final DependentAxis parent;

  public DeleteDatasetFromAxisCommand(Dataset dataset, DependentAxis parent)
  {
    this.dataset = dataset;
    this.parent = parent;
  }

  @Override
  public void execute()
  {
    parent.getDatasets().remove(dataset);
  }

  @Override
  public void undo()
  {
    parent.getDatasets().add(dataset);
  }
}
