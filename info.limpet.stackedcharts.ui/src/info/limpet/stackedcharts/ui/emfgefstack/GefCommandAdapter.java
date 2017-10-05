package info.limpet.stackedcharts.ui.emfgefstack;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.common.command.Command;

/**
 * Adapts a GEF {@link org.eclipse.gef.commands.Command} to the {@link Command} interface of EMF
 */
public class GefCommandAdapter implements Command
{
  private org.eclipse.gef.commands.Command command;

  public GefCommandAdapter(org.eclipse.gef.commands.Command command)
  {
    this.command = command;
  }

  public boolean canExecute()
  {
    return command == null ? false : command.canExecute();
  }

  public boolean canUndo()
  {
    return command == null ? false : command.canUndo();
  }

  public Command chain(Command command)
  {
    Command chained = this;
    if (command != null)
    {
      org.eclipse.gef.commands.Command gefCommand = null;
      if (command instanceof GefCommandAdapter)
      {
        gefCommand = ((GefCommandAdapter) command).getGefCommand();
      }
      else
      {
        gefCommand = new EmfCommandAdapter(command);
      }
      gefCommand = this.command.chain(gefCommand);
      if (gefCommand != null)
      {
        if (gefCommand instanceof EmfCommandAdapter)
        {
          chained = ((EmfCommandAdapter) gefCommand).unwrap();
        }
        else
        {
          chained = new GefCommandAdapter(gefCommand);
        }
      }
    }
    return chained;
  }

  public void dispose()
  {
    if (command != null)
      command.dispose();
  }

  public void execute()
  {
    if (command != null)
      command.execute();
  }

  public Collection getAffectedObjects()
  {
    return Collections.EMPTY_LIST;
  }

  public String getDescription()
  {
    return this.getClass().getName();
  }

  public String getLabel()
  {
    return command == null ? getDescription() : command.getLabel();
  }

  public Collection getResult()
  {
    return Collections.EMPTY_LIST;
  }

  public void redo()
  {
    if (command != null)
      command.redo();
  }

  public void undo()
  {
    if (command != null)
      command.undo();
  }

  public org.eclipse.gef.commands.Command getGefCommand()
  {
    return command;
  }
}
