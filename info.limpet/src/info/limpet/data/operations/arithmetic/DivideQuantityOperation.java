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
import info.limpet.ITemporalQuantityCollection;
import info.limpet.IStore.IStoreItem;
import info.limpet.ITemporalQuantityCollection.InterpMethod;
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

public class DivideQuantityOperation implements IOperation<IStoreItem>
{
  private final CollectionComplianceTests aTests = new CollectionComplianceTests();

  public Collection<ICommand<IStoreItem>> actionsFor(List<IStoreItem> selection, IStore destination,
      IContext context)
  {
    Collection<ICommand<IStoreItem>> res = new ArrayList<ICommand<IStoreItem>>();

    if (appliesTo(selection))
    {
      ICollection item1 = (ICollection) selection.get(0);
      ICollection item2 = (ICollection) selection.get(1);

      final IBaseTemporalCollection longest;

      if (aTests.allTemporal(selection))
      {
        longest = aTests.getLongestTemporalCollections(selection);
      }
      else
      {
        longest = null;
      }

      ICommand<IStoreItem> newC = new DivideQuantityValues("Divide " + item1.getName() + " by "
          + item2.getName(), selection, item1, item2, destination, longest, context);
      res.add(newC);
      newC = new DivideQuantityValues("Divide " + item2.getName() + " by " + item1.getName(), selection,
          item2, item1, destination, longest, context);
      res.add(newC);
    }

    return res;
  }

  private boolean appliesTo(List<IStoreItem> selection)
  {
    boolean res = false;
    // first check we have quantity data
    if (aTests.exactNumber(selection, 2))
    {
      if (aTests.allQuantity(selection))
      {
        // ok, we have quantity data. See if we have series of the same
        // length,
        // or
        // singletons
        res = aTests.allTemporal(selection) || aTests.allEqualLengthOrSingleton(selection);
      }
    }

    return res;
  }

  public static class DivideQuantityValues extends AbstractCommand<IStoreItem>
  {
    private final IQuantityCollection<Quantity> _item1;
    private final IQuantityCollection<Quantity> _item2;
    private IBaseTemporalCollection _timeProvider;

    @SuppressWarnings("unchecked")
    public DivideQuantityValues(String title, List<IStoreItem> selection, ICollection item1,
        ICollection item2, IStore store, IBaseTemporalCollection timeProvider, IContext context)
    {
      super(title, "Divide provided series", store, false, false, selection, context);
      _item1 = (IQuantityCollection<Quantity>) item1;
      _item2 = (IQuantityCollection<Quantity>) item2;
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
    public void execute()
    {
      List<IStoreItem> outputs = new ArrayList<IStoreItem>();

      // ok, generate the new series
      IQuantityCollection<?> target = createQuantityTarget();

      outputs.add(target);

      // store the output
      super.addOutput(target);

      // start adding values.
      performCalc(outputs);

      // tell each series that we're a dependent
      Iterator<IStoreItem> iter = getInputs().iterator();
      while (iter.hasNext())
      {
        ICollection iCollection = (ICollection) iter.next();
        iCollection.addDependent(this);
      }

      // ok, done
      List<IStoreItem> res = new ArrayList<IStoreItem>();
      res.add(target);
      getStore().addAll(res);
    }

    @SuppressWarnings("unchecked")
    private Unit<Quantity> calculateOutputUnit()
    {
      return (Unit<Quantity>) _item1.getUnits().divide(_item2.getUnits());
    }

    @Override
    protected void recalculate()
    {
      // update the results
      performCalc(getOutputs());
    }

    /**
     * wrap the actual operation. We're doing this since we need to separate it from the core "execute"
     * operation in order to support dynamic updates
     * @param outputs
     */
    @SuppressWarnings("unchecked")
    private void performCalc(List<IStoreItem> outputs)
    {
      IQuantityCollection<Quantity> target = (IQuantityCollection<Quantity>) outputs.iterator().next();

      // clear out the lists, first
      Iterator<IStoreItem> iter = outputs.iterator();
      while (iter.hasNext())
      {
        IQuantityCollection<Quantity> qC = (IQuantityCollection<Quantity>) iter.next();
        qC.clearQuiet();
      }

      if (_timeProvider != null)
      {
        // ok, temporal (interpolated) calculation
        Collection<Long> times = _timeProvider.getTimes();
        Iterator<Long> tIter = times.iterator();
        while (tIter.hasNext())
        {
          final Long thisTime = tIter.next();
          Double runningTotal = null;

          final double thisValue, otherValue;

          if (_item1.size() == 1)
          {
            thisValue = _item1.getValues().get(0).doubleValue((Unit<Quantity>) _item1.getUnits());
          }
          else
          {
            ITemporalQuantityCollection<Quantity> tqc = 
                (ITemporalQuantityCollection<Quantity>) _item1;
            Measurable<Quantity> thisMeasure = 
                tqc.interpolateValue(thisTime, InterpMethod.Linear);
            if (thisMeasure != null)
            {
              thisValue = thisMeasure.doubleValue(_item1.getUnits());
            }
            else
            {
              thisValue = 1;
            }
          }

          if (_item2.size() == 1)
          {
            otherValue = _item2.getValues().get(0).doubleValue((Unit<Quantity>) _item2.getUnits());
          }
          else
          {
            ITemporalQuantityCollection<Quantity> tqc = (ITemporalQuantityCollection<Quantity>) _item2;
            Measurable<Quantity> thisMeasure = tqc.interpolateValue(thisTime, InterpMethod.Linear);
            if (thisMeasure != null)
            {
              otherValue = thisMeasure.doubleValue(_item2.getUnits());
            }
            else
            {
              otherValue = 1;
            }
          }

          // first value?
          runningTotal = thisValue / otherValue;

          ITemporalQuantityCollection<?> itq = (ITemporalQuantityCollection<?>) target;
          itq.add(thisTime, runningTotal);
        }

      }
      else
      {

        // find the (non-singleton) array length
        final int length = getNonSingletonArrayLength(getInputs());

        for (int j = 0; j < length; j++)
        {
          final double thisValue;
          if (_item1.size() == 1)
          {
            thisValue = _item1.getValues().get(0).doubleValue((Unit<Quantity>) _item1.getUnits());
          }
          else
          {
            thisValue = _item1.getValues().get(j).doubleValue((Unit<Quantity>) _item1.getUnits());
          }

          final double otherValue;
          if (_item2.size() == 1)
          {
            otherValue = _item2.getValues().get(0).doubleValue((Unit<Quantity>) _item2.getUnits());
          }
          else
          {
            otherValue = _item2.getValues().get(j).doubleValue((Unit<Quantity>) _item2.getUnits());
          }

          double res = thisValue / otherValue;

          target.add(res);
        }
      }
    }

    @Override
    protected String getOutputName()
    {
      return getContext().getInput("Divide datasets", NEW_DATASET_MESSAGE, _item1 + " div by " + _item2);
    }
  }

}
