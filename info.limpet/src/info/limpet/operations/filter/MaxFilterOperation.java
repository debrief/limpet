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
package info.limpet.operations.filter;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.Document;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.Range;
import info.limpet.impl.UIProperty;
import info.limpet.operations.AbstractCommand;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.operations.RangedEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

public class MaxFilterOperation implements IOperation
{
  public static class FilterCollectionCommand extends AbstractCommand implements
      RangedEntity
  {

    final private FilterOperation operation;

    private final NumberDocument filterValue;

    public FilterCollectionCommand(final String title,
        final List<IStoreItem> selection, final IStoreGroup store,
        final IContext context, final FilterOperation operation,
        final NumberDocument filterValue)
    {
      super(title, "Filter documents", store, false, false, selection, context);
      this.operation = operation;
      this.filterValue = filterValue;
    }

    @Override
    public void execute()
    {
      // tell each series that we're a dependent
      for (final IStoreItem t : getInputs())
      {
        t.addChangeListener(this);
      }

      // create a filtered set of inputs
      final List<IStoreItem> filteredInputs =
          getFilteredInputs(getInputs(), filterValue);

      // create the outputs
      for (final IStoreItem t : filteredInputs)
      {
        final NumberDocument thisN = (NumberDocument) t;
        final NumberDocument thisO =
            new NumberDocument(null, this, thisN.getUnits());
        this.addOutput(thisO);
        t.addChangeListener(this);
      }

      // ok, go for it
      performCalc();

      // specify the index units
      final Iterator<IStoreItem> iIter = filteredInputs.iterator();
      final Iterator<Document<?>> oIter = getOutputs().iterator();
      while (iIter.hasNext())
      {
        final NumberDocument thisI = (NumberDocument) iIter.next();
        final NumberDocument thisO = (NumberDocument) oIter.next();

        final Unit<?> inUnits = thisI.getIndexUnits();
        if (inUnits != null)
        {
          thisO.setIndexUnits(thisI.getIndexUnits());
        }

        // and the output name
        thisO.setName(nameFor(thisI));
      }

      // ok, put the outputs into the store
      for (final Document<?> out : getOutputs())
      {
        getStore().add(out);
        out.fireDataChanged();
      }

    }

    @UIProperty(name = "MaxFilter", category = UIProperty.CATEGORY_CALCULATION,
        min = 1, max = 200)
    public int getAngleBinSize()
    {
      return (int) filterValue.getValueAt(0);
    }

    @Override
    public double getValue()
    {
      return (int) filterValue.getValue();
    }

    protected String nameFor(final NumberDocument input)
    {
      return operation.nameFor(input.getName(), (int) this.filterValue
          .getValue());
    }

    private void performCalc()
    {
      // ok, loop through the inputs
      final Iterator<IStoreItem> inpIter =
          getFilteredInputs(getInputs(), filterValue).iterator();
      final Iterator<Document<?>> outIter = getOutputs().iterator();

      while (inpIter.hasNext())
      {
        final NumberDocument thisIn = (NumberDocument) inpIter.next();
        final NumberDocument thisOut = (NumberDocument) outIter.next();

        final List<Double> vOut = new ArrayList<Double>();
        final List<Double> iOut = new ArrayList<Double>();

        // loop through the values
        final Iterator<Double> vIter = thisIn.getIterator();
        final Iterator<Double> iIter = thisIn.getIndexIterator();

        while (vIter.hasNext() && iIter.hasNext())
        {
          final double thisV = vIter.next();
          final double thisI = iIter.next();

          if (operation.keep(thisI, thisV, this.filterValue.getValue()))
          {
            vOut.add(thisV);
            iOut.add(thisI);
          }
        }

        // ok, handle a zero length list

        final DoubleDataset outD;
        final DoubleDataset outIndex;
        if (vOut.size() > 0)
        {
          outD = (DoubleDataset) DatasetFactory.createFromObject(vOut);
          outIndex = (DoubleDataset) DatasetFactory.createFromObject(iOut);
        }
        else
        {
          final List<Double> dList = new ArrayList<Double>();
          outD = DatasetFactory.createFromList(DoubleDataset.class, dList);
          outIndex = DatasetFactory.createFromList(DoubleDataset.class, dList);
        }

        // ok, create the output dataset

        final AxesMetadata am = new AxesMetadataImpl();
        am.initialize(1);
        am.setAxis(0, outIndex);
        outD.addMetadata(am);

        // and corret the name
        outD.setName(nameFor(thisIn));

        // and store it
        thisOut.setDataset(outD);
      }
    }

    @Override
    public void recalculate(final IStoreItem subject)
    {
      // ok, we need to recalculate
      performCalc();

      // tell the outputs they've changed
      for (final Document<?> out : getOutputs())
      {
        out.fireDataChanged();
      }
    }

    public void setAngleBinSize(final int val)
    {
      filterValue.setValue(val);

      // ok, fire update
      this.recalculate(null);
    }

    @Override
    public void setValue(final double value)
    {
      filterValue.setValue(value);
    }

    @Override
    public Range getRange()
    {
      return new Range(0, 200);
    }

  }

  private static interface FilterOperation
  {
    String getName();

    /**
     * should we keep this data value?
     * 
     * @param index
     * @param value
     * @param filterValue
     * @return
     */
    boolean keep(double index, double value, double filterValue);

    /**
     * produce an output name for this input file and filter value
     * 
     * @param name
     * @param filterValue
     * @return
     */
    String nameFor(String name, Integer filterValue);
  }

  private static class MaxFilter implements FilterOperation
  {
    @Override
    public String getName()
    {
      return "Apply max filter";
    }

    @Override
    public boolean keep(final double index, final double value,
        final double filterValue)
    {
      return value <= filterValue;
    }

    @Override
    public String nameFor(final String name, final Integer filterValue)
    {
      return name + " Max Filtered";
    }
  };

  private static class MinFilter implements FilterOperation
  {
    @Override
    public String getName()
    {
      return "Apply min filter";
    }

    @Override
    public boolean keep(final double index, final double value,
        final double filterValue)
    {
      return value >= filterValue;
    }

    @Override
    public String nameFor(final String name, final Integer filterValue)
    {
      return name + " Min Filtered";
    }
  }

  /**
   * utility method to return a set of inputs, without the filter document
   * 
   * @param list
   * @param filterValue
   * @return
   */
  private static List<IStoreItem> getFilteredInputs(
      final List<IStoreItem> list, final NumberDocument filterValue)
  {
    final List<IStoreItem> filteredInputs = new ArrayList<IStoreItem>();
    filteredInputs.addAll(list);
    filteredInputs.remove(filterValue);
    return filteredInputs;
  }

  @Override
  public List<ICommand> actionsFor(final List<IStoreItem> selection,
      final IStoreGroup destination, final IContext context)
  {
    final List<ICommand> res = new ArrayList<ICommand>();
    if (appliesTo(selection))
    {
      // create a cloned list of the selection
      final List<IStoreItem> filterSelection = new ArrayList<IStoreItem>();
      filterSelection.addAll(selection);

      // ok, find the singleton(s)
      NumberDocument singleton = null;
      for (final IStoreItem item : selection)
      {
        final NumberDocument doc = (NumberDocument) item;
        if (doc.size() == 1)
        {
          singleton = doc;
          break;
        }
      }
      final FilterOperation maxFilter = new MaxFilter();
      ICommand newC =
          new FilterCollectionCommand(maxFilter.getName(), filterSelection,
              destination, context, maxFilter, singleton);
      res.add(newC);

      // and the min filter
      final FilterOperation minFilter = new MinFilter();
      newC =
          new FilterCollectionCommand(minFilter.getName(), filterSelection,
              destination, context, minFilter, singleton);
      res.add(newC);
    }

    return res;
  }

  private boolean appliesTo(final List<IStoreItem> selection)
  {
    // check they all are numeric
    final CollectionComplianceTests aTests = new CollectionComplianceTests();

    final boolean allNumeric = aTests.allQuantity(selection);

    // check we have a singleton
    final boolean hasSingleton = aTests.hasSingleton(selection);

    return hasSingleton && allNumeric && selection.size() > 0;
  }

}
