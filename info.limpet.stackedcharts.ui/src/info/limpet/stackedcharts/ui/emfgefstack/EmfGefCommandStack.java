package info.limpet.stackedcharts.ui.emfgefstack;

import java.util.EventObject;
import java.util.Iterator;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;

/**
 * An EMF {@link org.eclipse.emf.common.command.CommandStack} wrapping a GEF {@link CommandStack}.
 * Source: http://www.eclipsezone.com/eclipse/forums/t46479.html
 */
public class EmfGefCommandStack extends BasicCommandStack
{
  private CommandStack wrappedGefCommandStack = new CommandStack()
  {
    public boolean canRedo()
    {
      return EmfGefCommandStack.this.canRedo();
    }

    public boolean canUndo()
    {
      return EmfGefCommandStack.this.canUndo();
    }

    public void execute(Command command)
    {
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
        EmfGefCommandStack.this.execute(emfCommand);
      }
    }

    public void flush()
    {
      EmfGefCommandStack.this.flush();
    }

    public Command getRedoCommand()
    {
      org.eclipse.emf.common.command.Command command = EmfGefCommandStack.this
          .getRedoCommand();
      Command gefCommand = null;
      if (command != null)
      {
        if (command instanceof GefCommandAdapter)
        {
          gefCommand = ((GefCommandAdapter) command).getGefCommand();
        }
        else
        {
          gefCommand = new EmfCommandAdapter(command);
        }
      }
      return gefCommand;
    }

    public Command getUndoCommand()
    {
      return null;
    }

    public void redo()
    {
      EmfGefCommandStack.this.redo();
    }

    public void undo()
    {
      EmfGefCommandStack.this.undo();
    }

  };

  public CommandStack getWrappedGefCommandStack()
  {
    return wrappedGefCommandStack;
  }

  protected void notifyListeners()
  {
    for (Iterator i = listeners.iterator(); i.hasNext();)
    {
      Object listener = i.next();
      if (listener instanceof org.eclipse.emf.common.command.CommandStackListener)
      {
        ((org.eclipse.emf.common.command.CommandStackListener) listener)
            .commandStackChanged(new EventObject(this));
      }
      else
      {
        ((CommandStackListener) listener).commandStackChanged(new EventObject(
            wrappedGefCommandStack));
      }
    }
  }

}
