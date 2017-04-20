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
import info.limpet.UIProperty;
import info.limpet2.ICommand;
import info.limpet2.IOperation;
import info.limpet2.IStoreGroup;
import info.limpet2.IStoreItem;
import info.limpet2.operations.AbstractCommand;
import info.limpet2.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.List;

public class SimpleMovingAverageOperation implements IOperation
{
  public static final String SERIES_NAME_TEMPLATE = "Simple Moving Average";

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  private final int _windowSize;

  public SimpleMovingAverageOperation(int windowSize)
  {
    this._windowSize = windowSize;
  }

  public List<ICommand> actionsFor(
      List<IStoreItem> selection, IStoreGroup destination, IContext context)
  {
    List<ICommand> res =
        new ArrayList<ICommand>();
    if (appliesTo(selection))
    {
      ICommand newC =
          new SimpleMovingAverageCommand(SERIES_NAME_TEMPLATE, selection,
              destination, _windowSize, context);
      res.add(newC);
    }

    return res;
  }

  private boolean appliesTo(List<IStoreItem> selection)
  {
    boolean singleSeries = selection.size() == 1;
    boolean allQuantity = aTests.allQuantity(selection);
    return singleSeries && allQuantity;
  }

  public static class SimpleMovingAverageCommand extends
      AbstractCommand
  {

    private int winSize;

    public SimpleMovingAverageCommand(String operationName,
        List<IStoreItem> selection, IStoreGroup store, int windowSize,
        IContext context)
    {
      super(operationName, "Calculates a Simple Moving Average", store, false,
          false, selection, context);
      winSize = windowSize;
    }

    @UIProperty(name = "Window", category = UIProperty.CATEGORY_CALCULATION,
        min = 1, max = 20)
    public int getWindowSize()
    {
      return winSize;
    }

    @Override
    public void recalculate(IStoreItem subject)
    {
      // TODO Auto-generated method stub
      
    }

    protected String getOutputName()
    {
      return getContext().getInput("Generate simple moving average",
          NEW_DATASET_MESSAGE, "Moving average of " + super.getSubjectList());
    }

    public void setWindowSize(int winSize)
    {
      this.winSize = winSize;

      // ok, we now need to update!
      super.dataChanged(this.getOutputs().iterator().next());
    }
//    
//    protected NumberDocument getOutputFor(NumberDocument input, String outName)
//    {
//      final IQuantityCollection<?> res;
//      
//      if(input.isTemporal())
//      {
//        @SuppressWarnings("unchecked")
//        Unit<Quantity> units = (Unit<Quantity>) input.getUnits();
//        res = new TemporalQuantityCollection<Quantity>(outName, this, units);
//      }
//      else
//      {
//        res = new QuantityCollection<>(outName, this, input.getUnits());
//      }
//      
//      return res;
//    }
//
//    @Override
//    public void execute()
//    {
//      IQuantityCollection<?> input =
//          (IQuantityCollection<?>) getInputs().get(0);
//
//      List<ICollection> outputs = new ArrayList<ICollection>();
//
//      // ok, generate the new series
//      IQuantityCollection<?> target = getOutputFor(input, getOutputName());
//
//      outputs.add(target);
//
//      // store the output
//      super.addOutput(target);
//
//      // start adding values.
//      performCalc(outputs);
//
//      // tell each series that we're a dependent
//      Iterator<ICollection> iter = getInputs().iterator();
//      while (iter.hasNext())
//      {
//        ICollection iCollection = iter.next();
//        iCollection.addDependent(this);
//      }
//
//      // ok, done
//      List<IStoreItem> res = new ArrayList<IStoreItem>();
//      res.add(target);
//      getStore().addAll(res);
//    }
//
//    @Override
//    public void recalculate(IStoreItem subject)
//    {
//      // update the results
//      performCalc(getOutputs());
//    }

//    /**
//     * wrap the actual operation. We're doing this since we need to separate it from the core
//     * "execute" operation in order to support dynamic updates
//     * 
//     * @param unit
//     * @param outputs
//     */
//    private void performCalc(List<ICollection> outputs)
//    {
//      IQuantityCollection<?> target =
//          (IQuantityCollection<?>) outputs.iterator().next();
//
//      // clear out the lists, first
//      Iterator<ICollection> iter = outputs.iterator();
//      while (iter.hasNext())
//      {
//        IQuantityCollection<?> qC = (IQuantityCollection<?>) iter.next();
//        qC.clearQuiet();
//      }
//
//      SimpleMovingAverage sma = new SimpleMovingAverage(winSize);
//      @SuppressWarnings("unchecked")
//      
//      IQuantityCollection<Quantity> input =
//          (IQuantityCollection<Quantity>) getInputs().get(0);
//      
//      if(input.isTemporal())
//      {
//        // use temporal data
//        ITemporalQuantityCollection<?> outT = (ITemporalQuantityCollection<?>) target;
//        ITemporalQuantityCollection<?> inT = (ITemporalQuantityCollection<?>) input;
//        
//        // we need our time data
//        List<Long> times = inT.getTimes();        
//        Iterator<Long> tIter = times.iterator();
//        for(Measurable<Quantity> quantity : input.getValues())
//        {
//          sma.newNum(quantity.doubleValue(input.getUnits()));
//          outT.add(tIter.next(), sma.getAvg());
//        }
//      }
//      else
//      {
//        // ok, plain values
//        for (Measurable<Quantity> quantity : input.getValues())
//        {
//          sma.newNum(quantity.doubleValue(input.getUnits()));
//          target.add(sma.getAvg());
//        }
//      }
//      
//
//      
//      // and fire the update
//      target.fireDataChanged();
//
//    }
  }

}
