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

public class MultiplyQuantityOperation extends BinaryQuantityOperation
{

  @Override
  protected void addInterpolatedCommands(List<IStoreItem> selection,
      IStoreGroup destination, Collection<ICommand> res, IContext context)
  {
    IDocument longest = getLongestIndexedCollection(selection);

    if (longest != null)
    {
      ICommand newC =
          new MultiplyQuantityValues(
              "Multiply numeric values in provided series (interpolated)",
              selection, destination, longest, context);
      res.add(newC);
    }
  }

  protected void addIndexedCommands(List<IStoreItem> selection,
      IStoreGroup destination, Collection<ICommand> res, IContext context)
  {
    ICommand newC =
        new MultiplyQuantityValues(
            "Multiply numeric values in provided series (indexed)", selection,
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

    return nonEmpty && allQuantity && suitableLength;
  }

  public class MultiplyQuantityValues extends BinaryQuantityCommand
  {
    public MultiplyQuantityValues(String name, List<IStoreItem> selection,
        IStoreGroup store, IContext context)
    {
      this(name, selection, store, null, context);
    }

    public MultiplyQuantityValues(String name, List<IStoreItem> selection,
        IStoreGroup destination, IDocument timeProvider, IContext context)
    {
      super(name, "Multiply datasets", destination, false, false, selection,
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
          return Maths.multiply(a, b, o);
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
      return "Product of " + name1 + " + " + name2;
    }
  }

}