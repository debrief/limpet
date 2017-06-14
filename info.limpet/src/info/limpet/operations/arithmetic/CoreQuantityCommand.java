package info.limpet.operations.arithmetic;

import info.limpet.IContext;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.Document;
import info.limpet.operations.AbstractCommand;

import java.util.Iterator;
import java.util.List;

public abstract class CoreQuantityCommand extends AbstractCommand
{

  public CoreQuantityCommand(final String title, final String description,
      final IStoreGroup store, final boolean canUndo, final boolean canRedo,
      final List<IStoreItem> inputs, final IContext context)
  {
    super(title, description, store, canUndo, canRedo, inputs, context);
  }

  /**
   * empty the contents of any results collections
   * 
   * @param outputs
   */
  protected void clearOutputs(final List<Document<?>> outputs)
  {
    // clear out the lists, first
    final Iterator<Document<?>> iter = outputs.iterator();
    while (iter.hasNext())
    {
      final Document<?> qC = iter.next();
      qC.clearQuiet();
    }
  }

  @Override
  public abstract void execute();

}
