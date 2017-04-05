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

import info.limpet2.Document;
import info.limpet2.ICommand;
import info.limpet2.IContext;
import info.limpet2.IStoreGroup;
import info.limpet2.IStoreItem;
import info.limpet2.NumberDocument;
import info.limpet2.operations.AbstractCommand;
import info.limpet2.operations.CollectionComplianceTests;
import info.limpet2.operations.arithmetic.InterpolatedMaths.IOperationPerformer;

import java.util.ArrayList;
import java.util.Collection;
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
import org.eclipse.january.dataset.Maths;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

public abstract class CoreQuantityOperation
{

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  public Collection<ICommand> actionsFor(List<Document> selection,
      IStoreGroup destination, IContext context)
  {
    Collection<ICommand> res = new ArrayList<ICommand>();
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
          || getATests().hasIndexed(selection)
          && getATests().allEqualLengthOrSingleton(selection))
      {
        addInterpolatedCommands(selection, destination, res, context);
      }

    }

    return res;
  }

  protected Document getLongestIndexedCollection(List<Document> selection)
  {
    // find the longest time series.
    Iterator<Document> iter = selection.iterator();
    Document longest = null;

    while (iter.hasNext())
    {
      Document doc = iter.next();
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
    return longest;
  }

  /**
   * determine if this dataset is suitable
   * 
   * @param selection
   * @return
   */
  protected abstract boolean appliesTo(List<Document> selection);

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
  protected abstract void addIndexedCommands(List<Document> selection,
      IStoreGroup destination, Collection<ICommand> commands, IContext context);

  /**
   * add any commands that require temporal interpolation
   * 
   * @param selection
   * @param destination
   * @param res
   */
  protected abstract void addInterpolatedCommands(List<Document> selection,
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
  public abstract class CoreQuantityCommand extends AbstractCommand
  {

    @SuppressWarnings("unused")
    private final Document timeProvider;

    public CoreQuantityCommand(String title, String description,
        IStoreGroup store, boolean canUndo, boolean canRedo,
        List<Document> inputs, IContext context)
    {
      this(title, description, store, canUndo, canRedo, inputs, null, context);
    }

    public CoreQuantityCommand(String title, String description,
        IStoreGroup store, boolean canUndo, boolean canRedo,
        List<Document> inputs, Document timeProvider, IContext context)
    {
      super(title, description, store, canUndo, canRedo, inputs, context);

      this.timeProvider = timeProvider;
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

    /**
     * wrap the actual operation. We're doing this since we need to separate it from the core
     * "execute" operation in order to support dynamic updates
     * 
     * @param unit
     *          the units to use
     * @param outputs
     *          the list of output series
     */
    protected IDataset performCalc()
    {
      final IDataset res;

      final IDataset in1 = getInputs().get(0).getDataset();
      final IDataset in2 = getInputs().get(1).getDataset();

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
              // ok, check they overlap
              // TODO: once January sample code is reviewed, insert it here

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
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        
        if(ind2 != null)
        {
          // apply our operation to the two datasets
          res =
              InterpolatedMaths.performWithInterpolation(ind1, ind2, null,
                  doAdd);
        }
        else
        {
          res =null;
        }

      }
      else if (getATests().allEqualLengthOrSingleton(getInputs()))
      {
        // ok, is one a singleton?

        // ok, can't interpolate. are they the same size?
        // ok, just do plain add
        res = Maths.add(in1, in2);
        
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

      if (res != null)
      {
        // store the name
        res.setName("Sum of " + in1.getName() + " + " + in2.getName());
      }

      // and fire out the update
      for (Document output : getOutputs())
      {
        output.fireDataChanged();
      }

      // done
      return res;
    }

    abstract protected  IOperationPerformer getOperation();

    protected int numElements()
    {
      int res = 0;

      // we may have a singleton array. select the non singleton array
      Iterator<Document> iter = getInputs().iterator();
      while (iter.hasNext())
      {
        IStoreItem item = (IStoreItem) iter.next();
        if (item instanceof Document)
        {
          Document doc = (Document) item;
          int thisSize = doc.size();
          res = Math.max(res, thisSize);
        }
      }
      return res;
    }

    // private void storeTemporalValue(IStoreItem target, long thisT, double val)
    // {
    // // TODO: we won't be running like this. we've got produce a whole
    // // output dataset, then store that
    // // ITemporalQuantityCollection<Q> qc =
    // // (ITemporalQuantityCollection<Q>) target;
    // // qc.add(thisT, Measure.valueOf(val, determineOutputUnit(target)));
    // }

    // /**
    // * store this value into the target (optionally including temporal aspects)
    // *
    // * @param target
    // * destination
    // * @param count
    // * index for this value
    // * @param value
    // * the value to store
    // */
    // private void storeValue(IStoreItem target, int count, Double value)
    // {
    // // TODO: we won't be doing it like this.
    // // if (target.isTemporal())
    // // {
    // // // ok, the input and output arrays must be temporal.
    // // ITemporalQuantityCollection<Q> qc =
    // // (ITemporalQuantityCollection<Q>) target;
    // // ITemporalQuantityCollection<Q> qi =
    // // (ITemporalQuantityCollection<Q>) getInputs().get(0);
    // // Long[] timeData = qi.getTimes().toArray(new Long[]
    // // {});
    // // qc.add(timeData[count], Measure.valueOf(value,
    // // determineOutputUnit(target)));
    // // }
    // // else
    // // {
    // // target.add(Measure.valueOf(value, determineOutputUnit(target)));
    // // }
    // }

    // /**
    // * produce a calculated value for the relevant index of the first input collection
    // *
    // * @param elementCount
    // * @return
    // */
    // protected abstract Double calcThisElement(int elementCount);

    // /**
    // * produce a calculated value for the relevant index of the first input collection
    // *
    // * @param elementCount
    // * @return
    // */
    // protected abstract Double calcThisInterpolatedElement(long time);

    @Override
    protected void recalculate(IStoreItem subject)
    {
      // calculate the results
      IDataset newSet = performCalc();

      // store the new dataset
      getOutputs().get(0).setDataset(newSet);
    }

    // /**
    // * produce a target of the correct type
    // *
    // * @param input
    // * one of the input series
    // * @param unit
    // * the units to use
    // * @return
    // */
    // @Deprecated
    // protected Document createQuantityTarget(IStoreItem input, Unit<?> unit)
    // {
    // // TODO: change this - it's actually got to happen once we've generated
    // // all of the output data
    // // double check the name is ok
    // final String outName = getOutputName();
    //
    // Document target = null;
    //
    // // if (outName != null)
    // // {
    // // if (timeProvider != null)
    // // {
    // // target = new TemporalQuantityCollection<Q>(outName, this, unit);
    // // }
    // // else
    // // {
    // // target = new QuantityCollection<Q>(outName, this, unit);
    // // }
    // // }
    //
    // return target;
    // }

    @Override
    public void execute()
    {
      // get the unit
      IStoreItem first = getInputs().get(0);

      // sort out the output unit
      Unit<?> unit = determineOutputUnit(first);

      // clear the results sets
      clearOutputs(getOutputs());

      // start adding values.
      IDataset dataset = performCalc();

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

    protected Unit<?> determineOutputUnit(IStoreItem first)
    {
      Unit<?> res = null;
      if (first instanceof NumberDocument)
      {
        NumberDocument doc = (NumberDocument) first;
        res = doc.getUnits();
      }
      return res;
    }

  }

}
