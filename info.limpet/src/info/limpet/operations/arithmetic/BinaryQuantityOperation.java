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
package info.limpet.operations.arithmetic;

import info.limpet.Document;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.NumberDocument;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.operations.arithmetic.InterpolatedMaths.IOperationPerformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.Comparisons;
import org.eclipse.january.dataset.Comparisons.Monotonicity;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

public abstract class BinaryQuantityOperation implements IOperation
{

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  @Override
  public List<ICommand> actionsFor(List<IStoreItem> selection,
      IStoreGroup destination, IContext context)
  {
    List<ICommand> res = new ArrayList<ICommand>();
    if (appliesTo(selection))
    {
      // so, do we do our indexed commands?
      if (getATests().allEqualLengthOrSingleton(selection))
      {
        addIndexedCommands(selection, destination, res, context);
      }

      // aah, what about temporal (interpolated) values?
      if (getATests().allIndexed(selection)
          && getATests().suitableForIndexedInterpolation(selection)
          || getATests().hasIndexed(selection))
      {
        addInterpolatedCommands(selection, destination, res, context);
      }
    }
    return res;
  }

  /** produce a reversed version of the supplied list
   * 
   * @param list
   * @return
   */
  protected List<IStoreItem> reverse(List<IStoreItem> list)
  {
    ArrayList<IStoreItem> res = new ArrayList<IStoreItem>(list);
    Collections.reverse(res);
    return res;
  }
  
  protected Document getLongestIndexedCollection(List<IStoreItem> selection)
  {
    // find the longest time series.
    Document longest = null;

    for(final IStoreItem sItem: selection)
    {
      if(sItem instanceof Document)
      {
        final Document doc = (Document) sItem;
        if (doc.isIndexed())
        {
          if (longest == null)
          {
            longest = doc;
          }
          else
          {
            // store the longest one
            longest = doc.size() > longest.size() ? doc : longest;
          }
        }
      }
    }
    
    return longest;
  }

  /**
   * determine if this dataset is suitable
   * 
   * @param selection
   * @return
   */
  protected abstract boolean appliesTo(List<IStoreItem> selection);

  /**
   * produce any new commands for this s election
   * 
   * @param selection
   *          current selection
   * @param destination
   *          where the results will end up
   * @param commands
   *          the list of commands
   */
  protected abstract void addIndexedCommands(List<IStoreItem> selection,
      IStoreGroup destination, Collection<ICommand> commands, IContext context);

  /**
   * add any commands that require temporal interpolation
   * 
   * @param selection
   * @param destination
   * @param res
   */
  protected abstract void addInterpolatedCommands(List<IStoreItem> selection,
      IStoreGroup destination, Collection<ICommand> res, IContext context);

  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

  /**
   * the command that actually produces data
   * 
   * @author ian
   * 
   */
  public abstract class BinaryQuantityCommand extends CoreQuantityCommand
  {

    @SuppressWarnings("unused")
    private final Document timeProvider;

    public BinaryQuantityCommand(String title, String description,
        IStoreGroup store, boolean canUndo, boolean canRedo,
        List<IStoreItem> inputs, IContext context)
    {
      this(title, description, store, canUndo, canRedo, inputs, null, context);
    }

    public BinaryQuantityCommand(String title, String description,
        IStoreGroup store, boolean canUndo, boolean canRedo,
        List<IStoreItem> inputs, Document timeProvider, IContext context)
    {
      super(title, description, store, canUndo, canRedo, inputs, context);

      this.timeProvider = timeProvider;
    }

    /**
     * for binary operations we act on a set of inputs, so, if one has changed then we will
     * recalculate all of them.
     */
    protected void recalculate(IStoreItem subject)
    {
      // calculate the results
      IDataset newSet = performCalc();

      // store the new dataset
      getOutputs().get(0).setDataset(newSet);
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
      Iterator<IStoreItem> iter = getInputs().iterator();
      while (iter.hasNext())
      {
        IStoreItem sItem = iter.next();
        if(sItem instanceof Document)
        {
          Document iCollection = (Document) sItem;
          iCollection.addDependent(this);          
        }
      }

      // ok, done
      getStore().add(output);
    }

    /**
     * wrap the actual operation. We're doing this since we need to separate it from the core
     * "execute" operation in order to support dynamic updates. That is, we need to create it when
     * run initially, then re-generate it on data updates
     * 
     * @param unit
     *          the units to use
     * @param outputs
     *          the list of output series
     */
    protected IDataset performCalc()
    {
      final IDataset res;

      final IDataset in1 = ((NumberDocument) getInputs().get(0)).getDataset();
      final IDataset in2 = ((NumberDocument) getInputs().get(1)).getDataset();

      // look for axes metadata
      final AxesMetadata axis1 = in1.getFirstMetadata(AxesMetadata.class);

      // keep track of the indices to use in the output
      final Dataset outputIndices;

      AxesMetadata axis2 = null;

      final boolean doInterp;

      if (axis1 != null)
      {
        // ok, is that axis monotonic?
        final Monotonicity axis1Mono =
            Comparisons.findMonotonicity(axis1.getAxes()[0]);

        if (axis1Mono.equals(Monotonicity.NOT_ORDERED))
        {
          // ok, not ordered. we can't use it
          doInterp = false;
          outputIndices = null;
        }
        else
        {
          axis2 = in2.getFirstMetadata(AxesMetadata.class);

          if (axis2 == null || axis2.getAxes() == null
              || axis2.getAxes().length == 0)
          {
            // the axes don't match. We can't do interp
            doInterp = false;
            // we'll use A indices in the output
            outputIndices = (Dataset) axis1.getAxes()[0];
          }
          else
          {
            final Monotonicity axis2Mono =
                Comparisons.findMonotonicity(axis2.getAxes()[0]);

            if (axis1.getAxes()[0].equals(axis2.getAxes()[0]))
            {
              // identical indexes, we don't need to intepolate
              doInterp = false;
              outputIndices = (Dataset) axis1.getAxes()[0];
            }
            else if (axis2Mono.equals(Monotonicity.NOT_ORDERED))
            {
              // ok, not ordered. we can't use it
              throw new IllegalArgumentException("Axes must be ordered");
            }
            else
            {
              // ok, are they in the same direction?
              if (axis1Mono.equals(axis2Mono))
              {
                // fake index
                outputIndices = (Dataset) axis1.getAxes()[0];

                // ok, do an interpolated add operation.
                doInterp = true;

              }
              else
              {
                // wrong directions, can't do
                doInterp = false;
                outputIndices = null;
              }
            }
          }
        }
      }
      else
      {
        doInterp = false;
        outputIndices = null;
      }

      if (doInterp)
      {
        final InterpolatedMaths.IOperationPerformer doAdd = getOperation();

        Dataset ind1 = null;
        Dataset ind2 = null;
        // extract the datasets
        try
        {
          // load the datasets
          ind1 = DatasetUtils.sliceAndConvertLazyDataset(in1);
          ind2 = DatasetUtils.sliceAndConvertLazyDataset(in2);
        }
        catch (DatasetException e)
        {
          e.printStackTrace();
        }

        if (ind2 != null)
        {
          // apply our operation to the two datasets
          res =
              InterpolatedMaths.performWithInterpolation(ind1, ind2, null,
                  doAdd);
        }
        else
        {
          res = null;
        }

      }
      else if (getATests().allEqualLengthOrSingleton(getInputs()))
      {
        // extract the datasets
        Dataset ind1 = null;
        Dataset ind2 = null;
        try
        {
          // load the datasets
          ind1 = DatasetUtils.sliceAndConvertLazyDataset(in1);
          ind2 = DatasetUtils.sliceAndConvertLazyDataset(in2);
        }
        catch (DatasetException de)
        {
          de.printStackTrace();
        }
        
        if(ind2 != null)
        {
          res = getOperation().perform(ind1, ind2, null);

          // if there are indices, store them
          if (outputIndices != null)
          {
            AxesMetadata am = new AxesMetadataImpl();
            am.initialize(1);
            am.setAxis(0, outputIndices);
            res.addMetadata(am);
          }
        }
        else
        {
          res = null;
        }
      }
      else
      {
        res = null;
      }

      // and fire out the update
      for (Document output : getOutputs())
      {
        output.fireDataChanged();
      }

      // done
      return res;
    }

    /**
     * provide class that can perform required operation
     * 
     * @return
     */
    abstract protected IOperationPerformer getOperation();

    protected Unit<?> getUnits()
    {
      // get the unit
      NumberDocument first = (NumberDocument) getInputs().get(0);
      NumberDocument second = (NumberDocument) getInputs().get(1);

      return getBinaryOutputUnit(first.getUnits(), second.getUnits());
    }

    protected String generateName()
    {
      // get the unit
      NumberDocument first = (NumberDocument) getInputs().get(0);
      NumberDocument second = (NumberDocument) getInputs().get(1);

      return getBinaryNameFor(first.getName(), second.getName());
    }

    /**
     * determine the units of the product
     * 
     * @param first
     * @param second
     * @return
     */
    abstract protected Unit<?>
        getBinaryOutputUnit(Unit<?> first, Unit<?> second);

    /**
     * provide the name for the product dataset
     * 
     * @param name
     * @param name2
     * @return
     */
    abstract protected String getBinaryNameFor(String name, String name2);
  }

}
