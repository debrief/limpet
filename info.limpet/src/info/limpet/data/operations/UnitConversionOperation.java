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
package info.limpet.data.operations;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.TemporalQuantityCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measurable;
import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public class UnitConversionOperation implements IOperation<ICollection>
{
  public static final String CONVERTED_TO = " converted to ";

  private final CollectionComplianceTests aTests = new CollectionComplianceTests();

  private final Unit<?> targetUnit;

  public UnitConversionOperation(Unit<?> targetUnit)
  {
    this.targetUnit = targetUnit;
  }

  public Collection<ICommand<ICollection>> actionsFor(
      List<ICollection> selection, IStore destination, IContext context)
  {
    Collection<ICommand<ICollection>> res =
        new ArrayList<ICommand<ICollection>>();
    if (appliesTo(selection))
    {
      String unitsName = targetUnit.toString();
      String name = "Convert to " + unitsName;
      String outputName = CONVERTED_TO + unitsName;
      ICommand<ICollection> newC =
          new ConvertQuanityValues(name, outputName, selection, destination,
              context);
      res.add(newC);
    }

    return res;
  }

  private boolean appliesTo(List<ICollection> selection)
  {
    boolean singleSeries = selection.size() == 1;
    boolean allQuantity = aTests.allQuantity(selection);
    boolean sameDimension = false;
    boolean sameUnits = true;
    if (selection.size() > 0)
    {
      if (allQuantity)
      {
        Unit<?> units = ((IQuantityCollection<?>) selection.get(0)).getUnits();
        sameDimension = units.getDimension().equals(targetUnit.getDimension());

        // check they're different units. It's not worth offering the
        // operation
        // if
        // they're already in the same units
        sameUnits = units.equals(targetUnit);
      }
    }
    return (singleSeries && allQuantity && sameDimension && !sameUnits);
  }

  public class ConvertQuanityValues extends AbstractCommand<ICollection>
  {

    public ConvertQuanityValues(String operationName, String outputName,
        List<ICollection> selection, IStore store, IContext context)
    {
      super(operationName, "Convert units of the provided series", store,
          false, false, selection, context);
    }

    @Override
    protected String getOutputName()
    {
      return getContext().getInput("Convert units", NEW_DATASET_MESSAGE,
          super.getSubjectList() + " converted to " + targetUnit);
    }

    @Override
    public void execute()
    {
      List<ICollection> outputs = new ArrayList<ICollection>();

      ICollection theInput = getInputs().iterator().next();

      // ok, generate the new series
      final IQuantityCollection<?> target;

      // hmm, is it a temporal operation
      if (theInput.isTemporal())
      {
        target =
            new TemporalQuantityCollection<>(getOutputName(), this, targetUnit);
      }
      else
      {
        target = new QuantityCollection<>(getOutputName(), this, targetUnit);
      }

      outputs.add(target);

      // store the output
      super.addOutput(target);

      // start adding values.
      performCalc(outputs);

      // tell each series that we're a dependent
      Iterator<ICollection> iter = getInputs().iterator();
      while (iter.hasNext())
      {
        ICollection iCollection = iter.next();
        iCollection.addDependent(this);
      }

      // ok, done
      List<IStoreItem> res = new ArrayList<IStoreItem>();
      res.add(target);
      getStore().addAll(res);
    }

    @Override
    protected void recalculate()
    {
      // update the results
      performCalc(getOutputs());
    }

    /**
     * wrap the actual operation. We're doing this since we need to separate it from the core
     * "execute" operation in order to support dynamic updates
     * 
     * @param unit
     * @param outputs
     */
    @SuppressWarnings("unchecked")
    private void performCalc(List<ICollection> outputs)
    {
      IQuantityCollection<Quantity> target =
          (IQuantityCollection<Quantity>) outputs.iterator().next();

      // clear out the lists, first
      Iterator<ICollection> iter = outputs.iterator();
      while (iter.hasNext())
      {
        IQuantityCollection<Quantity> qC =
            (IQuantityCollection<Quantity>) iter.next();
        qC.clearQuiet();
      }

      IQuantityCollection<Quantity> singleInputSeries =
          (IQuantityCollection<Quantity>) getInputs().get(0);

      UnitConverter converter =
          singleInputSeries.getUnits().getConverterTo(target.getUnits());

      for (int j = 0; j < singleInputSeries.getValues().size(); j++)
      {

        Measurable<Quantity> thisValue = singleInputSeries.getValues().get(j);
        double converted =
            converter.convert(thisValue.doubleValue(singleInputSeries
                .getUnits()));

        if (singleInputSeries.isTemporal())
        {
          TemporalQuantityCollection<Quantity> tq =
              (TemporalQuantityCollection<Quantity>) singleInputSeries;
          Long time = (Long) tq.getTimes().get(j);
          TemporalQuantityCollection<Quantity> tgtQ =
              (TemporalQuantityCollection<Quantity>) target;
          tgtQ.add(time, converted);
        }
        else
        {
          target.add(converted);

        }

      }
    }
  }

}
