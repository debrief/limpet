package info.limpet2.operations.arithmetic;

import info.limpet2.Document;
import info.limpet2.IContext;
import info.limpet2.IStoreGroup;
import info.limpet2.IStoreItem;
import info.limpet2.NumberDocument;
import info.limpet2.operations.AbstractCommand;

import java.util.Iterator;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;

public abstract class CoreQuantityCommand extends AbstractCommand
{

  public CoreQuantityCommand(String title, String description,
      IStoreGroup store, boolean canUndo, boolean canRedo,
      List<Document> inputs, IContext context)
  {
    super(title, description, store, canUndo, canRedo, inputs, context);
  }

  /** get the units for the product of this operation
   * 
   * @return
   */
  abstract protected Unit<?> getUnits();
  

  /** produce a name for the results dataset
   * 
   * @return
   */
  abstract protected String generateName();

  /** perform the calculation, get a new dataset results object
   * 
   * @return
   */
  abstract protected IDataset performCalc();

  
  @Override
  protected void recalculate(IStoreItem subject)
  {
    // calculate the results
    IDataset newSet = performCalc();

    // store the new dataset
    getOutputs().get(0).setDataset(newSet);
  }
  
  /**
   * empty the contents of any results collections
   * 
   * @param outputs
   */
  private void clearOutputs(List<Document> outputs)
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
  public void execute()
  {
    // sort out the output unit
    Unit<?> unit = getUnits();

    // clear the results sets
    clearOutputs(getOutputs());

    // start adding values.
    IDataset dataset = performCalc();
    
    // store the name
    dataset.setName(generateName());

    // ok, wrap the dataset
    NumberDocument output =
        new NumberDocument((DoubleDataset) dataset, this, unit);

    // and fire out the update
    output.fireDataChanged();

    // store the output
    super.addOutput(output);

    // tell each series that we're a dependent
    Iterator<Document> iter = getInputs().iterator();
    while (iter.hasNext())
    {
      Document iCollection = iter.next();
      iCollection.addDependent(this);
    }

    // ok, done
    getStore().add(output);
  }
}
