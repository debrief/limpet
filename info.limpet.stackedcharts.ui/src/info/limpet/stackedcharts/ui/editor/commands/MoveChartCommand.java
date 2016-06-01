package info.limpet.stackedcharts.ui.editor.commands;

import info.limpet.stackedcharts.model.Chart;

import java.util.List;

import org.eclipse.gef.commands.Command;

public class MoveChartCommand extends Command
{
  private final Chart chart;
  private final List<Chart> charts;
  private int index = -1;
  private int redoIndex = -1;

  public MoveChartCommand(final List<Chart> charts, final Chart chart,
      final int index)
  {
    this.chart = chart;
    this.charts = charts;
    this.index = index;
  }

  @Override
  public void execute()
  {
    redoIndex = charts.indexOf(chart);
    charts.remove(chart);
    if (index != -1)
    {
      charts.add(index, chart);
    }
    else
    {
      charts.add(chart);
    }
  }

  @Override
  public void undo()
  {
    index = charts.indexOf(chart);
    charts.remove(chart);
    if (redoIndex != -1)
    {
      charts.add(redoIndex, chart);
    }
    else
    {
      charts.add(chart);
    }
  }
}
