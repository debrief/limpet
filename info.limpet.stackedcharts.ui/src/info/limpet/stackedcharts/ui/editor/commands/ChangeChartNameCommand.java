package info.limpet.stackedcharts.ui.editor.commands;

import info.limpet.stackedcharts.model.Chart;

public class ChangeChartNameCommand extends ChartCommand<Chart, String>
{

  public ChangeChartNameCommand(Chart chart, String newName)
  {
    super(chart, newName);
  }

  @Override
  protected String getValue(Chart subject)
  {
    return subject.getName();
  }

  @Override
  protected void setValue(Chart subject, String attribute)
  {
    subject.setName(attribute);
  }
}
