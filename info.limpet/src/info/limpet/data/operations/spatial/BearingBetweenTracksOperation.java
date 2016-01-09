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
import info.limpet.IStore.IStoreItem;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.AngleDegrees;
import info.limpet.data.impl.samples.StockTypes.Temporal;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Angle;
import javax.measure.unit.Unit;

public class BearingBetweenTracksOperation extends TwoTrackOperation
{

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

        ICommand<IStoreItem> newC =
            new DistanceOperation(selection, destination,
                "Bearing between tracks (interpolated)",
                "Calculate bearing between two tracks (interpolated)",
                timeProvider, context)
            {

              protected IQuantityCollection<?> getOutputCollection(
                  String title, boolean isTemporal)
              {
                final IQuantityCollection<?> output;
                if (isTemporal)
                {
                  output = new StockTypes.Temporal.AngleDegrees(title, this);

                }
                else
                {
                  output = new StockTypes.NonTemporal.AngleDegrees(title, this);
                }

                return output;
              }

              @Override
              protected String getOutputName()
              {
                return getContext().getInput("Generate bearing",
                    NEW_DATASET_MESSAGE,
                    "Bearing between " + super.getSubjectList());
              }

              protected void calcAndStore(final GeoCalculator calc,
                  final Point2D locA, final Point2D locB, Long time)
              {
                // get the output dataset
                Temporal.AngleDegrees target2 =
                    (Temporal.AngleDegrees) getOutputs().get(0);
                Unit<Angle> outUnits = target2.getUnits();

                // now find the range between them
                double thisDist = calc.getAngleBetween(locA, locB);
                Measure<Double, Angle> outVar =
                    Measure.valueOf(thisDist, outUnits);

                target2.add(time, outVar);
              }
            };

        res.add(newC);
      }

      if (getATests().allEqualLengthOrSingleton(selection))
      {
        ICommand<IStoreItem> newC =
            new DistanceOperation(selection, destination,
                "Bearing between tracks (indexed)",
                "Calculate bearing between two tracks (indexed)", null, context)
            {

              protected IQuantityCollection<?> getOutputCollection(
                  String title, boolean isTemporal)
              {
                final IQuantityCollection<?> output;
                if (isTemporal)
                {
                  output = new StockTypes.Temporal.AngleDegrees(title, this);

                }
                else
                {
                  output = new StockTypes.NonTemporal.AngleDegrees(title, null);
                }

                return output;
              }

              @Override
              protected String getOutputName()
              {
                return getContext().getInput("Generate bearing",
                    NEW_DATASET_MESSAGE,
                    "Bearing between " + super.getSubjectList());
              }

              protected void calcAndStore(final GeoCalculator calc,
                  final Point2D locA, final Point2D locB, Long time)
              {
                // get the output dataset
                AngleDegrees target2 = (AngleDegrees) getOutputs().get(0);
                Unit<Angle> outUnits = target2.getUnits();

                // now find the range between them
                double thisDist = calc.getAngleBetween(locA,  locB);
                Measure<Double, Angle> outVar =
                    Measure.valueOf(thisDist, outUnits);

                // get the output dataset
                target2.add(outVar);

              }
            };

        res.add(newC);
      }
    }

    return res;
  }

}
