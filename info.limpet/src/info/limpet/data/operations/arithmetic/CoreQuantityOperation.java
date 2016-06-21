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
package info.limpet.data.operations.arithmetic;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStoreItem;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.TemporalQuantityCollection;
import info.limpet.data.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public abstract class CoreQuantityOperation<Q extends Quantity>
{

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  public Collection<ICommand<IQuantityCollection<Q>>> actionsFor(
      List<IQuantityCollection<Q>> selection, IStore destination,
      IContext context)
  {
    Collection<ICommand<IQuantityCollection<Q>>> res =
        new ArrayList<ICommand<IQuantityCollection<Q>>>();
    if (appliesTo(selection))
    {

      // so, do we do our indexed commands?
      if (getATests().allEqualLengthOrSingleton(selection))
      {
        addIndexedCommands(selection, destination, res, context);
      }

      // aah, what about temporal (interpolated) values?
      if (getATests().allTemporal(selection) && getATests()
          .suitableForTimeInterpolation(selection)
          || getATests().hasTemporal(selection) && getATests()
              .allEqualLengthOrSingleton(selection))
      {
        addInterpolatedCommands(selection, destination, res, context);
      }

    }

    return res;
  }

  protected ITemporalQuantityCollection<Q> getLongestTemporalCollections(
      List<IQuantityCollection<Q>> selection)
  {
    // find the longest time series.
    Iterator<IQuantityCollection<Q>> iter = selection.iterator();
    ITemporalQuantityCollection<Q> longest = null;

    while (iter.hasNext())
    {
      IQuantityCollection<Q> thisQ = iter.next();
      if (thisQ.isTemporal())
      {
        ITemporalQuantityCollection<Q> thisC =
            (ITemporalQuantityCollection<Q>) thisQ;
        if (longest == null)
        {
          longest = thisC;
        }
        else
        {
          // store the longest one
          longest = thisC.getValuesCount() > longest.getValuesCount() ? thisC : longest;
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
  protected abstract boolean appliesTo(List<IQuantityCollection<Q>> selection);

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
  protected abstract void addIndexedCommands(
      List<IQuantityCollection<Q>> selection, IStore destination,
      Collection<ICommand<IQuantityCollection<Q>>> commands, IContext context);

  /**
   * add any commands that require temporal interpolation
   * 
   * @param selection
   * @param destination
   * @param res
   */
  protected abstract void addInterpolatedCommands(
      List<IQuantityCollection<Q>> selection, IStore destination,
      Collection<ICommand<IQuantityCollection<Q>>> res, IContext context);

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
  public abstract class CoreQuantityCommand extends
      AbstractCommand<IQuantityCollection<Q>>
  {

    private final ITemporalQuantityCollection<Q> timeProvider;

    public CoreQuantityCommand(String title, String description, IStore store,
        boolean canUndo, boolean canRedo, List<IQuantityCollection<Q>> inputs,
        IContext context)
    {
      this(title, description, store, canUndo, canRedo, inputs, null, context);
    }

    public CoreQuantityCommand(String title, String description, IStore store,
        boolean canUndo, boolean canRedo, List<IQuantityCollection<Q>> inputs,
        ITemporalQuantityCollection<Q> timeProvider, IContext context)
    {
      super(title, description, store, canUndo, canRedo, inputs, context);

      this.timeProvider = timeProvider;
    }

    /**
     * empty the contents of any results collections
     * 
     * @param outputs
     */
    private void clearOutputs(List<IQuantityCollection<Q>> outputs)
    {
      // clear out the lists, first
      Iterator<IQuantityCollection<Q>> iter = outputs.iterator();
      while (iter.hasNext())
      {
        IQuantityCollection<Q> qC = (IQuantityCollection<Q>) iter.next();
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
    protected void performCalc(Unit<Q> unit,
        List<IQuantityCollection<Q>> outputs)
    {
      IQuantityCollection<Q> target = outputs.iterator().next();

      clearOutputs(outputs);

      if (timeProvider != null)
      {
        Collection<Long> times = timeProvider.getTimes();
        Iterator<Long> iter = times.iterator();
        while (iter.hasNext())
        {
          long thisT = (long) iter.next();
          Double val = calcThisInterpolatedElement(thisT);
          if (val != null)
          {
            storeTemporalValue(target, thisT, val);
          }
        }
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
      Iterator<IQuantityCollection<Q>> iter = getInputs().iterator();
      while (iter.hasNext())
      {
        IQuantityCollection<Q> iQuantityCollection =
            (IQuantityCollection<Q>) iter.next();
        int thisSize = iQuantityCollection.getValuesCount();
        res = Math.max(res, thisSize);
      }
      return res;
    }

    private void storeTemporalValue(IQuantityCollection<Q> target, long thisT,
        double val)
    {
      ITemporalQuantityCollection<Q> qc =
          (ITemporalQuantityCollection<Q>) target;
      qc.add(thisT, Measure.valueOf(val, determineOutputUnit(target)));
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
    private void storeValue(IQuantityCollection<Q> target, int count,
        Double value)
    {
      if (target.isTemporal())
      {
        // ok, the input and output arrays must be temporal.
        ITemporalQuantityCollection<Q> qc =
            (ITemporalQuantityCollection<Q>) target;
        ITemporalQuantityCollection<Q> qi =
            (ITemporalQuantityCollection<Q>) getInputs().get(0);
        Long[] timeData = qi.getTimes().toArray(new Long[]
        {});
        qc.add(timeData[count],
            Measure.valueOf(value, determineOutputUnit(target)));
      }
      else
      {
        target.add(Measure.valueOf(value, determineOutputUnit(target)));
      }
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
      IQuantityCollection<Q> first = getInputs().get(0);
      Unit<Q> unit = determineOutputUnit(first);

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
    protected IQuantityCollection<Q> createQuantityTarget(
        IQuantityCollection<Q> input, Unit<Q> unit)
    {
      // double check the name is ok
      final String outName = getOutputName();

      IQuantityCollection<Q> target = null;

      if (outName != null)
      {
        if (timeProvider != null)
        {
          target = new TemporalQuantityCollection<Q>(outName, this, unit);
        }
        else
        {
          target = new QuantityCollection<Q>(outName, this, unit);
        }
      }

      return target;
    }

    @Override
    public void execute()
    {
      // get the unit
      IQuantityCollection<Q> first = getInputs().get(0);

      List<IQuantityCollection<Q>> outputs =
          new ArrayList<IQuantityCollection<Q>>();

      // sort out the output unit
      Unit<Q> unit = determineOutputUnit(first);

      // ok, generate the new series
      final IQuantityCollection<Q> target = createQuantityTarget(first, unit);

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
      Iterator<IQuantityCollection<Q>> iter = getInputs().iterator();
      while (iter.hasNext())
      {
        ICollection iCollection = iter.next();
        iCollection.addDependent(this);
      }

      // ok, done
      getStore().add(target);
    }

    protected Unit<Q> determineOutputUnit(IQuantityCollection<Q> first)
    {
      return first.getUnits();
    }

  }

}
