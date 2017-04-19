/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet2.operations.arithmetic;

import info.limpet.IContext;
import info.limpet2.Document;
import info.limpet2.ICommand;
import info.limpet2.IOperation;
import info.limpet2.IStoreGroup;
import info.limpet2.IStoreItem;
import info.limpet2.NumberDocument;
import info.limpet2.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

public abstract class UnaryQuantityOperation implements IOperation
{
  private final String _opName;

  public UnaryQuantityOperation(String opName)
  {
    _opName = opName;
  }

  public String getName()
  {
    return _opName;
  }

  protected final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  public Collection<ICommand> actionsFor(List<IStoreItem> selection,
      IStoreGroup destination, IContext context)
  {
    Collection<ICommand> res = new ArrayList<ICommand>();
    if (appliesTo(selection))
    {
      ICommand newC =
          new UnaryQuantityCommand("Math - " + _opName, "description here",
              destination, selection, context);

      res.add(newC);
    }

    return res;
  }

  /**
   * determine if this dataset is suitable
   * 
   * @param selection
   * @return
   */
  protected abstract boolean appliesTo(List<IStoreItem> selection);

  /**
   * determine the units of the product
   * 
   * @param first
   * @param second
   * @return
   */
  abstract protected Unit<?> getUnaryOutputUnit(Unit<?> first);

  /**
   * provide the name for the product dataset
   * 
   * @param name
   * @param name2
   * @return
   */
  protected String getUnaryNameFor(String name)
  {
    return name + ": " + _opName;
  }

  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

  /**
   * perform the operation on the subject dataset
   * 
   * @param input
   * @return
   */
  abstract public Dataset calculate(Dataset input);

  /**
   * the command that actually produces data
   * 
   * @author ian
   * 
   */
  public class UnaryQuantityCommand extends CoreQuantityCommand
  {

    public UnaryQuantityCommand(String title, String description,
        IStoreGroup store, List<IStoreItem> inputs, IContext context)
    {
      super(title, description, store, true, true, inputs, context);
    }

    /**
     * for unitary operations we only act on a single input. We may be acting on an number of
     * datasets, so find the relevant one, and re-calculate it
     */
    protected void recalculate(IStoreItem subject)
    {
      // TODO: change logic, we should only re-generate the
      // single output
      
      // workaround: we don't know which output derives
      // from this input.  So, we will have to regenerate
      // all outputs

      Iterator<Document> oIter = getOutputs().iterator();
      
      // we may be acting separately on multiple inputs.
      // so, loop through them
      for (final IStoreItem input : getInputs())
      {
        final NumberDocument inputDoc = (NumberDocument) input;
        final NumberDocument outputDoc = (NumberDocument) oIter.next();

        // ok, process this one.
        Unit<?> unit = getUnits(inputDoc);
        
        // update the units
        if(outputDoc.getUnits() != unit)
        {
          outputDoc.setUnits(unit);
        }

        // clear the results sets
        clearOutputs(getOutputs());

        // start adding values.
        IDataset dataset = performCalc(inputDoc);

        // update the name
        dataset.setName(generateName(inputDoc));
        
        // store the data
        outputDoc.setDataset(dataset);

        // and fire out the update
        outputDoc.fireDataChanged();
      }
    }

    @Override
    public void execute()
    {
      // clear the results sets
      clearOutputs(getOutputs());

      // we may be acting separately on multiple inputs.
      // so, loop through them
      for (final IStoreItem input : getInputs())
      {
        final NumberDocument inputDoc = (NumberDocument) input;

        // ok, process this one.
        // sort out the output unit
        Unit<?> unit = getUnits(inputDoc);

        // start adding values.
        IDataset dataset = performCalc(inputDoc);

        // store the name
        dataset.setName(generateName(inputDoc));

        // ok, wrap the dataset
        NumberDocument output =
            new NumberDocument((DoubleDataset) dataset, this, unit);

        // and fire out the update
        output.fireDataChanged();

        // store the output
        super.addOutput(output);

        // tell the series that we're a dependent
        inputDoc.addDependent(this);

        // ok, store the results
        getStore().add(output);

      }

    }

    /**
     * wrap the actual operation. We're doing this since we need to separate it from the core
     * "execute" operation in order to support dynamic updates
     * 
     * @param nd
     * 
     * @param unit
     *          the units to use
     * @param outputs
     *          the list of output series
     */
    protected IDataset performCalc(NumberDocument nd)
    {
      final IDataset ids = nd.getDataset();
      Dataset res = null;
      try
      {
        final Dataset ds = DatasetUtils.sliceAndConvertLazyDataset(ids);

        // ok, re-calculate this
        res = calculate(ds);

        // store the axes
        final AxesMetadata axis1 = ds.getFirstMetadata(AxesMetadata.class);

        // if there are indices, store them
        if (axis1 != null)
        {
          AxesMetadata am = new AxesMetadataImpl();
          // keep track of the indices to use in the output
          final ILazyDataset outputIndicesLazy = axis1.getAxes()[0];
          Dataset outputIndices = null;
          try
          {
            outputIndices =
                DatasetUtils.sliceAndConvertLazyDataset(outputIndicesLazy);
          }
          catch (DatasetException e)
          {
            throw new IllegalArgumentException(
                "Unable to load axis for dataset:" + ds.getName());
          }
          am.initialize(1);
          am.setAxis(0, outputIndices);
          res.addMetadata(am);
        }
      }
      catch (DatasetException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      // done
      return res;
    }

    protected Unit<?> getUnits(NumberDocument inputDoc)
    {
      // get the unit
      return getUnaryOutputUnit(inputDoc.getUnits());
    }

    protected String generateName(NumberDocument inputDoc)
    {
      // get the name
      return getUnaryNameFor(inputDoc.getName());
    }

  }

}
