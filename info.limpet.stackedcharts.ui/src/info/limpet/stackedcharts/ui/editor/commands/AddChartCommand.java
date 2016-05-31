package info.limpet.stackedcharts.ui.editor.commands;

import info.limpet.stackedcharts.model.Chart;

import java.util.List;

import org.eclipse.gef.commands.Command;

public class AddChartCommand extends Command
{
  private final Chart chart;
  private final List<Chart> charts;
  private int index = -1;

  public AddChartCommand(List<Chart> charts, Chart chart)
  {
    this.chart = chart;
    this.charts = charts;
  }

  public AddChartCommand(List<Chart> charts, Chart chart, int index)
  {
    this.chart = chart;
    this.charts = charts;
    this.index = index;
  }

  @Override
  public void execute()
  {

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

  }
}
