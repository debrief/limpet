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

import info.limpet.IBaseTemporalCollection;
import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStoreItem;
import info.limpet.ITemporalQuantityCollection.InterpMethod;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.TemporalQuantityCollection;
import info.limpet.data.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public class MultiplyQuantityOperation implements IOperation<IStoreItem>
{
  private CollectionComplianceTests aTests = new CollectionComplianceTests();

  public MultiplyQuantityOperation()
  {
  }

  public Collection<ICommand<IStoreItem>> actionsFor(
      List<IStoreItem> selection, IStore destination, IContext context)
  {
    Collection<ICommand<IStoreItem>> res =
        new ArrayList<ICommand<IStoreItem>>();
    if (appliesTo(selection))
    {
      // ok, temporal?
      final boolean suitableForInterpolated = aTests.allTemporalOrSingleton(selection);
      final boolean suitableForIndexed =
          !aTests.allNonTemporal(selection)
              && aTests.allEqualLengthOrSingleton(selection);
      if (suitableForInterpolated || suitableForIndexed)
      {
        final IBaseTemporalCollection longest;
        if (suitableForInterpolated)
        {
          longest =
              (IBaseTemporalCollection) aTests
                  .getLongestTemporalCollections(selection);
        }
        else
        {
          longest = null;
        }

        ICommand<IStoreItem> newC =
            new MultiplyQuantityValues(selection, destination, longest, context);
        res.add(newC);
      }
      else
      {

        ICommand<IStoreItem> newC =
            new MultiplyQuantityValues(selection, destination, context);
        res.add(newC);
      }

    }

    return res;
  }

  private boolean appliesTo(List<IStoreItem> selection)
  {
    // first check we have quantity data
    if (aTests.allCollections(selection) && aTests.nonEmpty(selection)
        && aTests.allQuantity(selection))
    {
      // ok, we have quantity data. See if we have series of the same length, or
      // singletons
      return aTests.allTemporal(selection)
          || aTests.allEqualLengthOrSingleton(selection);
    }
    else
    {
      return false;
    }
  }

  public static class MultiplyQuantityValues extends
      AbstractCommand<IStoreItem>
  {

    private IBaseTemporalCollection _timeProvider;

    public MultiplyQuantityValues(List<IStoreItem> selection, IStore store,
        IContext context)
    {
      this(selection, store, null, context);
    }

    public MultiplyQuantityValues(List<IStoreItem> selection, IStore store,
        IBaseTemporalCollection timeProvider, IContext context)
    {
      super("Multiply series", "Multiply series", store, false, false,
          selection, context);
      _timeProvider = timeProvider;
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
    protected IQuantityCollection<?> createQuantityTarget()
    {
      Unit<?> unit = calculateOutputUnit();
      final IQuantityCollection<?> target;
      if (_timeProvider != null)
      {
        target = new TemporalQuantityCollection<>(getOutputName(), this, unit);
      }
      else
      {
        target = new QuantityCollection<>(getOutputName(), this, unit);
      }

      return target;
    }

    @Override
    protected String getOutputName()
    {
      return getContext().getInput("Multiply datasets", NEW_DATASET_MESSAGE,
          "Product of " + super.getSubjectList());
    }

    @Override
    public void execute()
    {
      Unit<?> unit = calculateOutputUnit();
      List<IStoreItem> outputs = new ArrayList<IStoreItem>();

      // ok, generate the new series
      IQuantityCollection<?> target = createQuantityTarget();

      outputs.add(target);

      // store the output
      super.addOutput(target);

      // start adding values.
      performCalc(unit, outputs);

      // tell each series that we're a dependent
      Iterator<IStoreItem> iter = getInputs().iterator();
      while (iter.hasNext())
      {
        ICollection iCollection = (ICollection) iter.next();
        iCollection.addDependent(this);
      }

      // ok, done
      getStore().add(target);
    }

    private Unit<?> calculateOutputUnit()
    {
      Iterator<IStoreItem> inputsIterator = getInputs().iterator();
      IQuantityCollection<?> firstItem =
          (IQuantityCollection<?>) inputsIterator.next();
      Unit<?> unit = firstItem.getUnits();

      while (inputsIterator.hasNext())
      {
        IQuantityCollection<?> nextItem =
            (IQuantityCollection<?>) inputsIterator.next();
        unit = unit.times(nextItem.getUnits());
      }
      return unit;
    }

    @Override
    protected void recalculate(IStoreItem subject)
    {
      Unit<?> unit = calculateOutputUnit();

      // update the results
      performCalc(unit, getOutputs());
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
    protected void performCalc(Unit<?> unit, List<IStoreItem> outputs)
    {
      IQuantityCollection<?> target =
          (IQuantityCollection<?>) outputs.iterator().next();

      // clear out the output list first
      target.clearQuiet();

      if (_timeProvider != null)
      {
        Collection<Long> times = _timeProvider.getTimes();
        Iterator<Long> tIter = times.iterator();
        while (tIter.hasNext())
        {
          final Long thisTime = tIter.next();
          Double runningTotal = null;

          for (int i = 0; i < getInputs().size(); i++)
          {
            @SuppressWarnings("unchecked")
            IQuantityCollection<Quantity> thisC =
                (IQuantityCollection<Quantity>) getInputs().get(i);

            final double thisValue;

            // just check that this isn't a singleton
            if (thisC.getValuesCount() == 1)
            {
              thisValue =
                  thisC.getValues().get(0).doubleValue(thisC.getUnits());
            }
            else
            {
              ITemporalQuantityCollection<Quantity> tqc =
                  (ITemporalQuantityCollection<Quantity>) thisC;
              Measurable<Quantity> thisMeasure =
                  tqc.interpolateValue(thisTime, InterpMethod.Linear);
              if (thisMeasure != null)
              {
                thisValue = thisMeasure.doubleValue(thisC.getUnits());
              }
              else
              {
                thisValue = 1;
              }
            }

            // first value?
            if (runningTotal == null)
            {
              runningTotal = thisValue;
            }
            else
            {
              runningTotal = runningTotal * thisValue;
            }
          }

          ITemporalQuantityCollection<?> itq =
              (ITemporalQuantityCollection<?>) target;
          itq.add(thisTime, runningTotal);
        }
      }
      else
      {
        // find the (non-singleton) array length
        int length = getNonSingletonArrayLength(getInputs());

        // start adding values.
        for (int j = 0; j < length; j++)
        {
          Double runningTotal = null;

          for (int i = 0; i < getInputs().size(); i++)
          {
            @SuppressWarnings("unchecked")
            IQuantityCollection<Quantity> thisC =
                (IQuantityCollection<Quantity>) getInputs().get(i);

            final double thisValue;

            // just check that this isn't a singleton
            if (thisC.getValuesCount() == 1)
            {
              thisValue =
                  thisC.getValues().get(0).doubleValue(thisC.getUnits());
            }
            else
            {
              thisValue =
                  thisC.getValues().get(j).doubleValue(thisC.getUnits());
            }

            // first value?
            if (runningTotal == null)
            {
              runningTotal = thisValue;
            }
            else
            {
              runningTotal = runningTotal * thisValue;
            }
          }

          target.add(runningTotal);
        }
      }

      target.fireDataChanged();
    }
  }
}
