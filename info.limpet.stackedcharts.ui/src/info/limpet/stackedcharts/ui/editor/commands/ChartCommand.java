package info.limpet.stackedcharts.ui.editor.commands;

import org.eclipse.gef.commands.Command;

public abstract class ChartCommand<Subject, Attribute> extends Command
{
  private final Subject subject;
  private final Attribute oldValue;
  private final Attribute newValue;

  public ChartCommand(Subject chart, Attribute value)
  {
    subject = chart;
    oldValue = getValue(chart);
    newValue = value;
  }
  
  /** get the existing value for the object
   * 
   * @param subject
   * @return
   */
  abstract protected Attribute getValue(Subject subject);
  
  /** set the new value for the object
   * 
   * @param subject the element we're looking at
   * @param attribute the value to assign
   */
  abstract protected void setValue(Subject subject, Attribute value);

  @Override
  public final void execute()
  {
    setValue(subject, newValue);
  }

  @Override
  public final void undo()
  {
    setValue(subject, oldValue);
  }
}
