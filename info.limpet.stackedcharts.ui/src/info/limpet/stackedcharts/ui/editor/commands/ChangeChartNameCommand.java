package info.limpet.stackedcharts.ui.editor.commands;

import info.limpet.stackedcharts.model.Chart;

import org.eclipse.gef.commands.Command;

public class ChangeChartNameCommand extends Command
{
  private final Chart chart;
  private final String oldName;
  private final String newName;

  public ChangeChartNameCommand(Chart chart, String newName)
  {
    this.chart = chart;
    this.oldName = chart.getName();
    this.newName = newName;
  }

  @Override
  public void execute()
  {
    chart.setName(newName);
  }

  @Override
  public void undo()
  {
    chart.setName(oldName);
  }
}
