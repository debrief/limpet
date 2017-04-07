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
package info.limpet2.operations.arithmetic.simple;

import info.limpet2.Document;
import info.limpet2.ICommand;
import info.limpet2.IContext;
import info.limpet2.IStoreGroup;
import info.limpet2.IStoreItem;
import info.limpet2.operations.arithmetic.BinaryQuantityOperation;
import info.limpet2.operations.arithmetic.InterpolatedMaths;
import info.limpet2.operations.arithmetic.InterpolatedMaths.IOperationPerformer;

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
    Document longest = getLongestIndexedCollection(selection);

    if (longest != null)
    {
      ICommand newC =
          new SubtractQuantityValues(
              "Subtract numeric values in provided series (interpolated)",
              selection, destination, longest, context);
      res.add(newC);
    }
  }

  protected void addIndexedCommands(List<IStoreItem> selection,
      IStoreGroup destination, Collection<ICommand> res, IContext context)
  {
    ICommand newC =
        new SubtractQuantityValues(
            "Subtract numeric values in provided series (indexed)", selection,
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
        IStoreGroup destination, Document timeProvider, IContext context)
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
      return "Sum of " + name1 + " + " + name2;
    }
  }

}
