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

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.operations.arithmetic.InterpolatedMaths.IOperationPerformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.eclipse.january.dataset.Comparisons;
import org.eclipse.january.dataset.Comparisons.Monotonicity;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.metadata.AxesMetadata;

public abstract class BulkQuantityOperation implements IOperation
{

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  /**
   * the command that actually produces data
   * 
   * @author ian
   * 
   */
  public abstract class BulkQuantityCommand extends CoreQuantityCommand
  {

    @SuppressWarnings("unused")
    private final IDocument<?> timeProvider;

    public BulkQuantityCommand(final String title, final String description,
        final IStoreGroup store, final boolean canUndo, final boolean canRedo,
        final List<IStoreItem> inputs, final IContext context)
    {
      this(title, description, store, canUndo, canRedo, inputs, null, context);
    }

    public BulkQuantityCommand(final String title, final String description,
        final IStoreGroup store, final boolean canUndo, final boolean canRedo,
        final List<IStoreItem> inputs, final IDocument<?> timeProvider,
        final IContext context)
    {
      super(title, description, store, canUndo, canRedo, inputs, context);

      this.timeProvider = timeProvider;
    }

    protected String generateName()
    {
      return getBulkNameFor(getInputs());
    }

    /**
     * provide the name for the product dataset
     * 
     * @param name
     * @param name2
     * @return
     */
    abstract protected String getBulkNameFor(List<IStoreItem> items);

    /**
     * determine the units of the product
     * 
     * @param first
     * @param second
     * @return
     */
    abstract protected Unit<?> getBulkOutputUnit(List<Unit<?>> units);

    /**
     * provide class that can perform required operation
     * 
     * @return
     */
    abstract protected IOperationPerformer getOperation();

    protected Unit<?> getUnits()
    {
      final List<Unit<?>> units = new ArrayList<Unit<?>>();
      for (IStoreItem input : getInputs())
      {
        final NumberDocument doc = (NumberDocument) input;
        units.add(doc.getUnits());
      }
      return getBulkOutputUnit(units);
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

      // keep track of the indices to use in the output
      boolean doInterp = false;
      Dataset outputIndices = null;

      
      // quick check
      if(getATests().allEqualIndexedOrSingleton(getInputs()))
      {
        doInterp = true;
        NumberDocument longestDoc = (NumberDocument) getLongestCollection(getInputs());
        outputIndices = longestDoc.getIndexValues();
      }
      else
      {
        // we're probably going to be interpolating
        // loop through the inputs
        DoubleDataset existingAxis = null;
        
        for (final IStoreItem item : getInputs())
        {
          final NumberDocument doc = (NumberDocument) item;

          final DoubleDataset first = (DoubleDataset) doc.getDataset();

          // look for axes metadata
          final AxesMetadata axis = first.getFirstMetadata(AxesMetadata.class);

          if (axis != null && axis.getAxes() != null
              && axis.getAxes().length == 1)
          {
            // ok, is that axis monotonic?
            final ILazyDataset thisAxis = axis.getAxes()[0];
            final Monotonicity axis1Mono = Comparisons.findMonotonicity(thisAxis);

            if (axis1Mono.equals(Monotonicity.NOT_ORDERED))
            {
              // ok, not ordered. we can't use it
              doInterp = false;

              // see if we have a set of output indices we can use
              outputIndices = findIndexDataset();

              // ok, we're done. we can drop out.
              break;
            }
            else
            {
              // do we have an existing axis?
              if (existingAxis == null)
              {
                existingAxis = (DoubleDataset) thisAxis;
              }
              else
              {
                // ok, check if they match
                if (existingAxis.equals(thisAxis) && doInterp)
                {
                  // identical indexes, we don't need to intepolate
                  doInterp = false;
                  outputIndices = (Dataset) thisAxis;
                }
                else
                {
                  doInterp = true;
                }
              }
            }
          }
        }
      }



      // how long it output dataset?
      final int shape;
      if (doInterp)
      {
        final NumberDocument longest =
            (NumberDocument) getLongestIndexedCollection(getInputs());
        shape = longest.getDataset().getShape()[0];

        if (outputIndices == null)
        {
          outputIndices = longest.getIndexValues();
        }
      }
      else
      {
        NumberDocument longest =
            (NumberDocument) getLongestIndexedCollection(getInputs());
        if(longest == null)
        {
          // no, no indexed data
          longest = (NumberDocument) getLongestCollection(getInputs());
        }
        shape = longest.getDataset().getShape()[0];

        if (outputIndices == null && longest.isIndexed())
        {
          outputIndices = longest.getIndexValues();
        }

      }

      if (doInterp)
      {
        final InterpolatedMaths.IOperationPerformer doAdd = getOperation();

        DoubleDataset current = getInitial(shape);
        // if there are indices, store them
        assignOutputIndices(current, outputIndices);

        for (IStoreItem item : getInputs())
        {
          NumberDocument doc = (NumberDocument) item;
          DoubleDataset thisD = (DoubleDataset) doc.getDataset();
          
          // hmm, is this a singleton?
          if(doc.size() == 1)
          {
            // if it's just a singleton, we'll add the same value to each 
            // results value
            current = (DoubleDataset) doAdd.perform(current,  thisD, null);
          }
          else
          {
            // apply our operation to the two datasets
            current =
                (DoubleDataset) InterpolatedMaths.performWithInterpolation(
                    current, thisD, null, doAdd);
          }
          

          // if there are indices, store them
          assignOutputIndices(current, outputIndices);
        }

        res = current;

      }
      else if (getATests().allEqualLengthOrSingleton(getInputs()))
      {

        DoubleDataset current = getInitial(shape);
        // if there are indices, store them
        assignOutputIndices(current, outputIndices);
        for (IStoreItem item : getInputs())
        {
          NumberDocument doc = (NumberDocument) item;
          DoubleDataset thisD = (DoubleDataset) doc.getDataset();
          current =
              (DoubleDataset) getOperation().perform(current, thisD, null);

          // if there are indices, store them
          assignOutputIndices(current, outputIndices);
        }

        res = current;
      }
      else
      {
        res = null;
      }

      // done
      return res;
    }

    /** we loop through the data, but we need an initial dataset to start from
     * 
     * @param shape number of entities to contain
     * @return
     */
    abstract protected DoubleDataset getInitial(int shape);
  }

  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

  @Override
  public List<ICommand> actionsFor(final List<IStoreItem> selection,
      final IStoreGroup destination, final IContext context)
  {
    final List<ICommand> res = new ArrayList<ICommand>();
    if (appliesTo(selection))
    {

      // aah, what about temporal (interpolated) values?
      final boolean allIndexed = getATests().allEqualIndexedOrSingleton(selection);
      final boolean suitableForIndexedInterpolation =
          getATests().suitableForIndexedInterpolation(selection);
      // final boolean hasIndexed = getATests().hasIndexed(selection);
      if (allIndexed && suitableForIndexedInterpolation /* || hasIndexed */)
      {
        addInterpolatedCommands(selection, destination, res, context);
      }
      else if (getATests().allEqualLengthOrSingleton(selection))
      {
        // instead, offer our indexed commands?
        addIndexedCommands(selection, destination, res, context);
      }
    }
    return res;
  }

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

  /**
   * determine if this dataset is suitable
   * 
   * @param selection
   * @return
   */
  protected abstract boolean appliesTo(List<IStoreItem> selection);

  protected IDocument<?> getLongestCollection(final List<IStoreItem> selection)
  {
    // find the longest time series.
    IDocument<?> longest = null;

    for (final IStoreItem sItem : selection)
    {
      if (sItem instanceof IDocument)
      {
        final IDocument<?> doc = (IDocument<?>) sItem;
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

  protected IDocument<?> getLongestIndexedCollection(
      final List<IStoreItem> selection)
  {
    // find the longest time series.
    IDocument<?> longest = null;

    for (final IStoreItem sItem : selection)
    {
      if (sItem instanceof IDocument)
      {
        final IDocument<?> doc = (IDocument<?>) sItem;
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
   * check if any of the data is decibels - since we can't do traditional add/subtract to them
   * 
   * @param selection
   * @return yes/no
   */
  protected boolean hasLogData(final List<IStoreItem> selection)
  {
    return aTests.isUnitPresent(selection, NonSI.DECIBEL);
  }

}
