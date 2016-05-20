package info.limpet.stackedcharts.ui.editor.commands;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.Command;

public class ChartCommand extends Command
{
  private final EObject subject;
  private final EAttribute attribute;
  private final Object oldValue;
  private final Object newValue;

  public ChartCommand(EObject chart, EAttribute attribute, Object theNewValue)
  {
    this.subject = chart;
    this.attribute = attribute;
    this.oldValue = subject.eGet(attribute);
    this.newValue = theNewValue;
  }

  @Override
  public final void execute()
  {
    subject.eSet(attribute, newValue);
  }

  @Override
  public final void undo()
  {
    subject.eSet(attribute, oldValue);
  }
}
