package info.limpet.stackedcharts.ui.emfgefstack;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.gef.commands.Command;

/**
 * Adapts an EMF {@link org.eclipse.emf.common.command.Command} to a GEF {@link Command}
 */
public class EmfCommandAdapter extends Command
{
  private org.eclipse.emf.common.command.Command command;

  public EmfCommandAdapter(org.eclipse.emf.common.command.Command command)
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
      org.eclipse.emf.common.command.Command emfCommand = null;
      if (command instanceof EmfCommandAdapter)
      {
        emfCommand = ((EmfCommandAdapter) command).unwrap();
      }
      else
      {
        emfCommand = new GefCommandAdapter(command);
      }
      emfCommand = this.command.chain(emfCommand);
      if (emfCommand != null)
      {
        if (emfCommand instanceof GefCommandAdapter)
        {
          chained = ((GefCommandAdapter) emfCommand).getGefCommand();
        }
        else
        {
          chained = new EmfCommandAdapter(emfCommand);
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

  public String getLabel()
  {
    return command == null ? this.getClass().getName() : command.getLabel();
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

  public Collection getAffectedObjects()
  {
    return command == null ? Collections.EMPTY_LIST : command
        .getAffectedObjects();
  }

  public String getDescription()
  {
    return command == null ? this.getClass().getName() : command
        .getDescription();
  }

  public Collection getResult()
  {
    return command == null ? Collections.EMPTY_LIST : command.getResult();
  }

  public org.eclipse.emf.common.command.Command unwrap()
  {
    return command;
  }

}