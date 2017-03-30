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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.unit.Unit;

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
      if (getATests().allTemporal(selection)
          && getATests().suitableForTimeInterpolation(selection)
          || getATests().hasTemporal(selection)
          && getATests().allEqualLengthOrSingleton(selection))
      {
        addInterpolatedCommands(selection, destination, res, context);
      }

    }

    return res;
  }

  protected Document getLongestTemporalCollections(List<Document> selection)
  {
    // find the longest time series.
    Iterator<Document> iter = selection.iterator();
    Document longest = null;

    while (iter.hasNext())
    {
      Document doc = iter.next();
      if (doc.isTemporal())
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
    protected void performCalc(Unit<?> unit, List<Document> outputs)
    {
      IStoreItem target = outputs.iterator().next();

      clearOutputs(outputs);

      if (timeProvider != null)
      {
        // TODO: sort out the timings
        // Collection<Long> times = timeProvider.getTimes();
        // Iterator<Long> iter = times.iterator();
        // while (iter.hasNext())
        // {
        // long thisT = (long) iter.next();
        // Double val = calcThisInterpolatedElement(thisT);
        // if (val != null)
        // {
        // storeTemporalValue(target, thisT, val);
        // }
        // }
      }
      else
      {
        int numItems = numElements();
        for (int elementCount = 0; elementCount < numItems; elementCount++)
        {
          Double thisResult = calcThisElement(elementCount);

          // ok, done - store it!
          storeValue(target, elementCount, thisResult);
        }
      }

      // and fire out the update
      target.fireDataChanged();

    }

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

    private void storeTemporalValue(IStoreItem target, long thisT, double val)
    {
      // TODO: we won't be running like this. we've got produce a whole
      // output dataset, then store that
      // ITemporalQuantityCollection<Q> qc =
      // (ITemporalQuantityCollection<Q>) target;
      // qc.add(thisT, Measure.valueOf(val, determineOutputUnit(target)));
    }

    /**
     * store this value into the target (optionally including temporal aspects)
     * 
     * @param target
     *          destination
     * @param count
     *          index for this value
     * @param value
     *          the value to store
     */
    private void storeValue(IStoreItem target, int count, Double value)
    {
      // TODO: we won't be doing it like this.
      // if (target.isTemporal())
      // {
      // // ok, the input and output arrays must be temporal.
      // ITemporalQuantityCollection<Q> qc =
      // (ITemporalQuantityCollection<Q>) target;
      // ITemporalQuantityCollection<Q> qi =
      // (ITemporalQuantityCollection<Q>) getInputs().get(0);
      // Long[] timeData = qi.getTimes().toArray(new Long[]
      // {});
      // qc.add(timeData[count], Measure.valueOf(value,
      // determineOutputUnit(target)));
      // }
      // else
      // {
      // target.add(Measure.valueOf(value, determineOutputUnit(target)));
      // }
    }

    /**
     * produce a calculated value for the relevant index of the first input collection
     * 
     * @param elementCount
     * @return
     */
    protected abstract Double calcThisElement(int elementCount);

    /**
     * produce a calculated value for the relevant index of the first input collection
     * 
     * @param elementCount
     * @return
     */
    protected abstract Double calcThisInterpolatedElement(long time);

    @Override
    protected void recalculate(IStoreItem subject)
    {
      // get the unit
      Document first = getInputs().get(0);
      Unit<?> unit = determineOutputUnit(first);

      // update the results
      performCalc(unit, getOutputs());
    }

    /**
     * produce a target of the correct type
     * 
     * @param input
     *          one of the input series
     * @param unit
     *          the units to use
     * @return
     */
    @Deprecated
    protected Document createQuantityTarget(IStoreItem input, Unit<?> unit)
    {
      // TODO: change this - it's actually got to happen once we've generated
      // all of the output data
      // double check the name is ok
      final String outName = getOutputName();

      Document target = null;

      // if (outName != null)
      // {
      // if (timeProvider != null)
      // {
      // target = new TemporalQuantityCollection<Q>(outName, this, unit);
      // }
      // else
      // {
      // target = new QuantityCollection<Q>(outName, this, unit);
      // }
      // }

      return target;
    }

    @Override
    public void execute()
    {
      // get the unit
      IStoreItem first = getInputs().get(0);

      List<Document> outputs = new ArrayList<Document>();

      // sort out the output unit
      Unit<?> unit = determineOutputUnit(first);

      // ok, generate the new series
      final Document target = createQuantityTarget(first, unit);

      if (target == null)
      {
        getContext().logError(IContext.Status.WARNING,
            "User cancelled create operation", null);
        return;
      }

      outputs.add(target);

      // store the output
      super.addOutput(target);

      // start adding values.
      performCalc(unit, outputs);

      // tell each series that we're a dependent
      Iterator<Document> iter = getInputs().iterator();
      while (iter.hasNext())
      {
        Document iCollection = iter.next();
        iCollection.addDependent(this);
      }

      // ok, done
      getStore().add(target);
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
