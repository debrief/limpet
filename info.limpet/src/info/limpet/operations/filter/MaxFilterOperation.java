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
import info.limpet.impl.UIProperty;
import info.limpet.operations.AbstractCommand;
import info.limpet.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

public class MaxFilterOperation implements IOperation
{
  public List<ICommand> actionsFor(List<IStoreItem> selection,
      IStoreGroup destination, IContext context)
  {
    List<ICommand> res = new ArrayList<ICommand>();
    if (appliesTo(selection))
    {
      final String commandTitle;
      commandTitle = "Apply max filter";
      ICommand newC =
          new FilterCollection(commandTitle, selection, destination, context);
      res.add(newC);
    }

    return res;
  }

  private boolean appliesTo(List<IStoreItem> selection)
  {
    // check they all are numeric
    CollectionComplianceTests aTests = new CollectionComplianceTests();

    final boolean allNumeric = aTests.allQuantity(selection);

    return allNumeric && selection.size() > 0;
  }

  public static class FilterCollection extends AbstractCommand
  {

    private Integer filterValue = null;

    public FilterCollection(String title, List<IStoreItem> selection,
        IStoreGroup store, IContext context)
    {
      super(title, "Apply max filter to collections", store, false, false,
          selection, context);
    }

    @UIProperty(name = "MaxFilter", category = UIProperty.CATEGORY_CALCULATION,
        min = 1, max = 200)
    public int getAngleBinSize()
    {
      return filterValue;
    }

    public void setAngleBinSize(int val)
    {
      filterValue = val;

      // ok, fire update
      this.recalculate(null);
    }
    
    @Override
    public void execute()
    {
      // get the number to use
      String numStr =
          super.getContext().getInput("Filter documents",
              "What is the maximum value to allow?", "100");
      if (numStr != null)
      {
        // get the value
        this.filterValue  = Integer.parseInt(numStr);

        // tell each series that we're a dependent
        for(IStoreItem t: getInputs())
        {
          t.addChangeListener(this);
        }
        
        // create the outputs
        for(IStoreItem t: getInputs())
        {
          NumberDocument thisN = (NumberDocument) t;
          NumberDocument thisO = new NumberDocument(null, this, thisN.getUnits());
          this.addOutput(thisO);
          t.addChangeListener(this);
        }
        
        // ok, go for it
        performCalc();

        // specify the index units
        Iterator<IStoreItem> iIter = getInputs().iterator();
        Iterator<Document<?>> oIter = getOutputs().iterator();
        while(iIter.hasNext())
        {
          NumberDocument thisI = (NumberDocument) iIter.next();
          NumberDocument thisO = (NumberDocument) oIter.next();
          thisO.setIndexUnits(thisI.getIndexUnits());
          
          // and the output name
          thisO.setName(nameFor(thisI));
        }

        // ok, put the outputs into the store
        for(Document<?> out: getOutputs())
        {
          getStore().add(out);
          out.fireDataChanged();
        }
      }
    }

    private void performCalc()
    {
      // ok, loop through the inputs
      Iterator<IStoreItem> inpIter = getInputs().iterator();
      Iterator<Document<?>> outIter = getOutputs().iterator();
      
      while(inpIter.hasNext())
      {
        NumberDocument thisIn = (NumberDocument) inpIter.next();
        NumberDocument thisOut = (NumberDocument) outIter.next();
        
        List<Double> vOut = new ArrayList<Double>();
        List<Double> iOut = new ArrayList<Double>();
        
        // loop through the values
        Iterator<Double> vIter = thisIn.getIterator();
        Iterator<Double> iIter = thisIn.getIndexIterator();
        
        while(vIter.hasNext())
        {
          double thisV = vIter.next();
          double thisI = iIter.next();
          
          if(keep(thisI, thisV))
          {
            vOut.add(thisV);
            iOut.add(thisI);
          }
        }
        
        // ok, create the output dataset
        DoubleDataset outD = (DoubleDataset) DatasetFactory.createFromObject(vOut);
        DoubleDataset outIndex = (DoubleDataset) DatasetFactory.createFromObject(iOut);
        AxesMetadata am = new AxesMetadataImpl();
        am.initialize(1);
        am.setAxis(0, outIndex);
        outD.addMetadata(am);
        
        // and corret the name
        outD.setName(nameFor(thisIn));
        
        // and store it
        thisOut.setDataset(outD);
      }
    }
    
    protected String nameFor(NumberDocument input)
    {
      return input.getName() + " MAX filter to:" + this.filterValue;
    }
    
    protected boolean keep(double index, double value)
    {
      return value <= filterValue;
    }

    @Override
    protected void recalculate(IStoreItem subject)
    {
      // ok, we need to recalculate
      performCalc();
      
      // tell the outputs they've changed
      for(Document<?> out: getOutputs())
      {
        out.fireDataChanged();
      }
    }

  }

}
