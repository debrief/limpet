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
package info.limpet.operations.arithmetic.simple;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.operations.arithmetic.BinaryQuantityOperation;
import info.limpet.operations.arithmetic.InterpolatedMaths;
import info.limpet.operations.arithmetic.InterpolatedMaths.IOperationPerformer;

import java.util.Collection;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.Maths;

public class SubtractQuantityOperation extends BinaryQuantityOperation
{

  @Override
  protected void addInterpolatedCommands(List<IStoreItem> selection,
      IStoreGroup destination, Collection<ICommand> res, IContext context)
  {
    IDocument longest = getLongestIndexedCollection(selection);

    if (longest != null)
    {
      IStoreItem doc1 = selection.get(0);
      IStoreItem doc2 = selection.get(1);

      ICommand newC =
          new SubtractQuantityValues(
              "Subtract " + doc2 + " from " + doc1 + "(interpolated)",
              selection, destination, longest, context);
      res.add(newC);
      
      newC =
          new SubtractQuantityValues(
              "Subtract " + doc1 + " from " + doc2 + "(interpolated)",
              reverse(selection), destination, longest, context);
      res.add(newC);
      
      
    }
  }

  protected void addIndexedCommands(List<IStoreItem> selection,
      IStoreGroup destination, Collection<ICommand> res, IContext context)
  {
    IStoreItem doc1 = selection.get(0);
    IStoreItem doc2 = selection.get(1);

    ICommand newC =
        new SubtractQuantityValues(
            "Subtract " + doc2 + " from " + doc1 + "(indexed)", selection,
            destination, context);
    res.add(newC);
    newC =
        new SubtractQuantityValues(
            "Subtract " + doc1 + " from " + doc2 + "(indexed)", reverse(selection),
            destination, context);
    res.add(newC);
  }

  protected boolean appliesTo(List<IStoreItem> selection)
  {
    boolean nonEmpty = getATests().nonEmpty(selection);
    boolean allQuantity = getATests().allQuantity(selection);
    boolean suitableLength =
        getATests().allIndexed(selection)
            || getATests().allEqualLengthOrSingleton(selection);
    boolean equalDimensions = getATests().allEqualDimensions(selection);
    boolean equalUnits = getATests().allEqualUnits(selection);

    return nonEmpty && allQuantity && suitableLength && equalDimensions
        && equalUnits;
  }

  public class SubtractQuantityValues extends BinaryQuantityCommand
  {
    public SubtractQuantityValues(String name, List<IStoreItem> selection,
        IStoreGroup store, IContext context)
    {
      this(name, selection, store, null, context);
    }

    public SubtractQuantityValues(String name, List<IStoreItem> selection,
        IStoreGroup destination, IDocument timeProvider, IContext context)
    {
      super(name, "Subtract datasets", destination, false, false, selection,
          timeProvider, context);
    }

    @Override
    protected IOperationPerformer getOperation()
    {
      return new InterpolatedMaths.IOperationPerformer()
      {
        @Override
        public Dataset perform(Dataset a, Dataset b, Dataset o)
        {
          return Maths.subtract(a, b, o);
        }
      };
    }

    @Override
    protected Unit<?> getBinaryOutputUnit(Unit<?> first, Unit<?> second)
    {
      // Subtraction doesn't modify units, just use first ones
      return first;
    }

    @Override
    protected String getBinaryNameFor(String name1, String name2)
    {
      return name1 + " subtracted from " + name2;
    }
  }

}
