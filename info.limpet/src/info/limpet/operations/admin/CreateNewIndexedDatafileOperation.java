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
package info.limpet.operations.admin;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.operations.arithmetic.BinaryQuantityOperation;
import info.limpet.operations.arithmetic.InterpolatedMaths;
import info.limpet.operations.arithmetic.InterpolatedMaths.IOperationPerformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

public class CreateNewIndexedDatafileOperation extends BinaryQuantityOperation
{
  
  @Override
  public List<ICommand> actionsFor(List<IStoreItem> selection,
      IStoreGroup destination, IContext context)
  {
    List<ICommand> res = new ArrayList<ICommand>();
    if (appliesTo(selection))
    {
      // aah, what about temporal (interpolated) values?
      addInterpolatedCommands(selection, destination, res, context);
    }
    return res;
  }

  @Override
  protected void addInterpolatedCommands(List<IStoreItem> selection,
      IStoreGroup destination, Collection<ICommand> res, IContext context)
  {
    IDocument<?> longest = getLongestIndexedCollection(selection);

    if (longest != null)
    {
      NumberDocument coll1 = (NumberDocument) selection.get(0);
      NumberDocument coll2 = (NumberDocument) selection.get(1);
      
      ICommand newC =
          new NewIndexedDatasetCommand(
              "Create new document, indexed on:" + coll1,
              selection, destination, longest, context);
      res.add(newC);
      
      ArrayList<IStoreItem> newSel = new ArrayList<IStoreItem>(selection);
      Collections.reverse(newSel);
      
      // ok, now reverse the selection
      newC =
          new NewIndexedDatasetCommand(
              "Create new document, indexed on:" + coll2,
              newSel, destination, longest, context);
      res.add(newC);
      
    }
  }

  protected void addIndexedCommands(List<IStoreItem> selection,
      IStoreGroup destination, Collection<ICommand> res, IContext context)
  {
    throw new RuntimeException("This operation doesn't support indexed operations");
  }

  protected boolean appliesTo(List<IStoreItem> selection)
  {
    boolean nonEmpty = getATests().nonEmpty(selection);
    boolean correctNum = getATests().exactNumber(selection, 2);
    boolean allQuantity = getATests().allQuantity(selection);
    boolean commonIndex = getATests().allIndexed(selection);

    return nonEmpty && correctNum && allQuantity && commonIndex;
  }

  public class NewIndexedDatasetCommand extends BinaryQuantityCommand
  {
    public NewIndexedDatasetCommand(String name, List<IStoreItem> selection,
        IStoreGroup store, IContext context)
    {
      this(name, selection, store, null, context);
    }

    public NewIndexedDatasetCommand(String name, List<IStoreItem> selection,
        IStoreGroup destination, IDocument<?> timeProvider, IContext context)
    {
      super(name, "Reindex dataset", destination, false, false, selection,
          timeProvider, context);
    }
    
    
   
    @Override
    protected Unit<?> getUnits()
    {
      // ok, now set the index units
      NumberDocument index = (NumberDocument) getInputs().get(1);
      return index.getUnits();
    }

    @Override
    protected void tidyOutput(NumberDocument output)
    {
      super.tidyOutput(output);
      
      // ok, now set the index units
      NumberDocument index = (NumberDocument) getInputs().get(0);
      Unit<?> indUnits = index.getUnits();
      
      // and store them
      output.setIndexUnits(indUnits);
    }

    @Override
    protected void assignOutputIndices(IDataset output, Dataset outputIndices)
    {
      // ok, we don't do this, we want to take charge of the output indices
    }

    @Override
    protected IOperationPerformer getOperation()
    {
      return new InterpolatedMaths.IOperationPerformer()
      {
        @Override
        public Dataset perform(Dataset a, Dataset b, Dataset o)
        {
          // ok, we're going to use dataset a as the new index units,
          // so we've just got to set them in a copy of b
          Dataset output = b.clone();
          
          // clear any existing metadata
          output.clearMetadata(AxesMetadata.class);
          
          // now store the new metadata
          AxesMetadata am = new AxesMetadataImpl();
          am.initialize(1);
          am.setAxis(0, a);
          output.addMetadata(am);
          
          return output;
        }
      };
    }

    @Override
    protected Unit<?> getBinaryOutputUnit(Unit<?> first, Unit<?> second)
    {
      // return product of units
      return first.times(second);
    }

    @Override
    protected String getBinaryNameFor(String name1, String name2)
    {
      return "Composite of " + name1 + " and " + name2;
    }
  }

}
