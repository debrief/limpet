package info.limpet2.operations.arithmetic;

import info.limpet.IContext;
import info.limpet2.Document;
import info.limpet2.IStoreGroup;
import info.limpet2.IStoreItem;
import info.limpet2.operations.AbstractCommand;

import java.util.Iterator;
import java.util.List;

public abstract class CoreQuantityCommand extends AbstractCommand
{

  public CoreQuantityCommand(String title, String description,
      IStoreGroup store, boolean canUndo, boolean canRedo,
      List<IStoreItem> inputs, IContext context)
  {
    super(title, description, store, canUndo, canRedo, inputs, context);
  }
  
  /**
   * empty the contents of any results collections
   * 
   * @param outputs
   */
  protected void clearOutputs(List<Document> outputs)
  {
    // clear out the lists, first
    Iterator<Document> iter = outputs.iterator();
    while (iter.hasNext())
    {
      Document qC = (Document) iter.next();
      qC.clearQuiet();
    }
  }

  @Override
  public abstract void execute();

}
