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
package info.limpet.data.operations.spatial;

import info.limpet.IBaseTemporalCollection;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStoreItem;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.LengthM;
import info.limpet.data.impl.samples.StockTypes.Temporal;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

public class DistanceBetweenTracksOperation extends TwoTrackOperation
{

  private final class DistanceBetweenOperation extends DistanceOperation
  {

    private DistanceBetweenOperation(List<IStoreItem> selection, IStore store,
        String title, String description, IContext context)
    {
      this(selection, store, title, description, null, context);
    }

    DistanceBetweenOperation(List<IStoreItem> selection, IStore store,
        String title, String description, IBaseTemporalCollection timeProvider,
        IContext context)
    {
      super(selection, store, title, description, timeProvider, context);
    }

    protected IQuantityCollection<?> getOutputCollection(String title,
        boolean isTemporal)
    {
      final IQuantityCollection<?> res;
      if (isTemporal)
      {
        res = new StockTypes.Temporal.LengthM(title, this);
      }
      else
      {
        res = new StockTypes.NonTemporal.LengthM(title, this);

      }

      return res;

    }

    protected void calcAndStore(final IGeoCalculator calc, final Point2D locA,
        final Point2D locB, Long time)
    {
      final Unit<Length> outUnits;
      if (time != null)
      {
        // get the output dataset
        Temporal.LengthM target2 = (Temporal.LengthM) getOutputs().get(0);
        outUnits = target2.getUnits();
      }
      else
      {
        // get the output dataset
        LengthM target2 = (LengthM) getOutputs().get(0);
        outUnits = target2.getUnits();
      }

      // now find the range between them
      double thisDist = calc.getDistanceBetween(locA, locB);
      final Measure<Double, Length> thisRes =
          Measure.valueOf(thisDist, outUnits);

      if (time != null)
      {
        // get the output dataset
        Temporal.LengthM target2 = (Temporal.LengthM) getOutputs().get(0);
        target2.add(time, thisRes);
      }
      else
      {
        // get the output dataset
        LengthM target2 = (LengthM) getOutputs().get(0);
        target2.add(thisRes);
      }
    }

    @Override
    protected String getOutputName()
    {
      return getContext().getInput("Distance between tracks",
          NEW_DATASET_MESSAGE, "Distance between " + super.getSubjectList());
    }
  }

  public Collection<ICommand<IStoreItem>> actionsFor(
      List<IStoreItem> selection, IStore destination, IContext context)
  {
    Collection<ICommand<IStoreItem>> res =
        new ArrayList<ICommand<IStoreItem>>();
    if (appliesTo(selection))
    {
      // ok, are we doing a tempoarl opeartion?
      if (getATests().suitableForTimeInterpolation(selection))
      {
        // hmm, find the time provider
        final IBaseTemporalCollection timeProvider =
            getATests().getLongestTemporalCollections(selection);

        // ok, provide an interpolated action
        ICommand<IStoreItem> newC =
            new DistanceBetweenOperation(selection, destination,
                "Distance between tracks (interpolated)",
                "Calculate distance between two tracks (interpolated)",
                timeProvider, context);
        res.add(newC);
      }

      if (getATests().allEqualLengthOrSingleton(selection))
      {
        // ok, provide an indexed action
        ICommand<IStoreItem> newC =
            new DistanceBetweenOperation(selection, destination,
                "Distance between tracks (indexed)",
                "Calculate distance between two tracks (indexed)", context);
        res.add(newC);
      }
    }

    return res;
  }
}
