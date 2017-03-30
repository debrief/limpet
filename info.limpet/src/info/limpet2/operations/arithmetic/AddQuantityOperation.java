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
import info.limpet2.IOperation;
import info.limpet2.IStoreGroup;

import java.util.Collection;
import java.util.List;

public class AddQuantityOperation extends
    CoreQuantityOperation implements IOperation
{

  @Override
  protected void addInterpolatedCommands(
      List<Document> selection, IStoreGroup destination,
      Collection<ICommand> res, IContext context)
  {
    Document longest = getLongestTemporalCollections(selection);

    if (longest != null)
    {
      ICommand newC = new AddQuantityValues(
          "Add numeric values in provided series (interpolated)", selection,
          destination, longest, context);
      res.add(newC);
    }
  }

  protected void addIndexedCommands(List<Document> selection,
      IStoreGroup destination, Collection<ICommand> res,
      IContext context)
  {
    ICommand newC = new AddQuantityValues(
        "Add numeric values in provided series (indexed)", selection,
        destination, context);
    res.add(newC);
  }

  protected boolean appliesTo(List<Document> selection)
  {
    boolean nonEmpty = getATests().nonEmpty(selection);
    boolean allQuantity = getATests().allQuantity(selection);
    boolean suitableLength = getATests().allTemporal(selection)
        || getATests().allEqualLengthOrSingleton(selection);
    boolean equalDimensions = getATests().allEqualDimensions(selection);
    boolean equalUnits = getATests().allEqualUnits(selection);

    return nonEmpty && allQuantity && suitableLength && equalDimensions && equalUnits;
  }

  public class AddQuantityValues extends CoreQuantityCommand
  {
    public AddQuantityValues(String name,
        List<Document> selection, IStoreGroup store, IContext context)
    {
      this(name, selection, store, null, context);
    }

    public AddQuantityValues(String name,
        List<Document> selection, IStoreGroup destination,
        Document timeProvider, IContext context)
    {
      super(name, "Add datasets", destination, false, false, selection,
          timeProvider, context);
    }

    @Override
    protected Double calcThisElement(int elementCount)
    {
      Double thisResult = null;

      for (int seriesCount = 0; seriesCount < getInputs().size(); seriesCount++)
      {
        // TODO: re-implement this.
//        Document thisC = getInputs().get(seriesCount);
//        Measurable<Q> thisV = thisC.size() == 1 ? thisC.getValues().get(0)
//            : (Measurable<Q>) thisC.getValues().get(elementCount);
//
//        // is this the first field?
//        if (thisResult == null)
//        {
//          thisResult = thisV.doubleValue(thisC.getUnits());
//        }
//        else
//        {
//          thisResult += thisV.doubleValue(thisC.getUnits());
//        }
      }
      return thisResult;
    }

    @Override
    protected Double calcThisInterpolatedElement(long time)
    {
      Double thisResult = null;
      
      // TODO: implement this

//      for (int seriesCount = 0; seriesCount < getInputs().size(); seriesCount++)
//      {
//        Document thisC = (Document) getInputs()
//            .get(seriesCount);
//
//        final Measurable<Q> thisV;
//
//        if (thisC.isTemporal())
//        {
//          // find the value to use
//          NumberDocument tq = (NumberDocument) thisC;
//          thisV = tq.interpolateValue(time, Document.InterpMethod.Linear);
//
//        }
//        else
//        {
//          if (thisC.size() == 1)
//          {
//            // ok, it's a singleton that we're applying to all values
//            thisV = thisC.getValues().get(0);
//          }
//          else
//          {
//            throw new RuntimeException(
//                "We should not be adding a non-singleton non-temporal to a temporal");
//          }
//        }
//
//        if (thisV != null)
//        {
//          // is this the first field?
//          if (thisResult == null)
//          {
//            thisResult = thisV.doubleValue(thisC.getUnits());
//          }
//          else
//          {
//            thisResult += thisV.doubleValue(thisC.getUnits());
//          }
//        }
//      }
      return thisResult;
    }

    @Override
    protected String getOutputName()
    {
      return getContext().getInput("Add datasets",
          "Please provide a name for the dataset",
          "Sum of " + super.getSubjectList());
    }
  }

}
