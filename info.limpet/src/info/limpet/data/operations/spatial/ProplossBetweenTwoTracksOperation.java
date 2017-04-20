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
import info.limpet.data.impl.samples.StockTypes.NonTemporal;
import info.limpet.data.impl.samples.StockTypes.Temporal;
import info.limpet.data.impl.samples.StockTypes.Temporal.AcousticStrength;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

public class ProplossBetweenTwoTracksOperation extends TwoTrackOperation
{

  private final class ProplossBetweenOperation extends DistanceOperation
  {
    private ProplossBetweenOperation(final List<IStoreItem> selection,
        final IStore store, final String title, final String description,
        final IBaseTemporalCollection timeProvider, final IContext context)
    {
      super(selection, store, title, description, timeProvider, context);
    }

    @Override
    protected void calcAndStore(final IGeoCalculator calc, final Point2D locA,
        final Point2D locB, final Long time)
    {
      final Unit<Dimensionless> outUnits = NonSI.DECIBEL;
      final double thisDistMetres = calc.getDistanceBetween(locA, locB);

      // ok, we've got to do 20 log R
      final double thisLoss = 20d * Math.log(thisDistMetres);
      final Measure<Double, Dimensionless> thisRes =
          Measure.valueOf(thisLoss, outUnits);

      if (time != null)
      {
        // get the output dataset
        final AcousticStrength target2 =
            (Temporal.AcousticStrength) getOutputs().get(0);
        target2.add(time, thisRes);
      }
      else
      {
        // get the output dataset
        final NonTemporal.AcousticStrength target2 =
            (NonTemporal.AcousticStrength) getOutputs().get(0);
        target2.add(thisRes);
      }
    }

    @Override
    protected IQuantityCollection<?> getOutputCollection(final String title,
        final boolean isTemporal)
    {
      final IQuantityCollection<?> res;
      if (isTemporal)
      {
        res = new StockTypes.Temporal.AcousticStrength(title, this);
      }
      else
      {
        res = new StockTypes.NonTemporal.AcousticStrength(title, this);
      }
      return res;
    }

    @Override
    protected String getOutputName()
    {
      return getContext().getInput("Generate propagation loss",
          NEW_DATASET_MESSAGE, "Proploss between " + super.getSubjectList());
    }
  }

  @Override
  public Collection<ICommand<IStoreItem>> actionsFor(
      final List<IStoreItem> rawSelection, final IStore destination,
      final IContext context)
  {
    final Collection<ICommand<IStoreItem>> res =
        new ArrayList<ICommand<IStoreItem>>();

    final List<IStoreItem> collatedTracks = getLocationDatasets(rawSelection);

    if (appliesTo(collatedTracks))
    {
      // ok, are we doing a tempoarl opeartion?
      if (getATests().suitableForTimeInterpolation(collatedTracks))
      {
        // hmm, find the time provider
        final IBaseTemporalCollection timeProvider =
            getATests().getLongestTemporalCollections(collatedTracks);

        final ICommand<IStoreItem> newC =
            new ProplossBetweenOperation(collatedTracks, destination,
                "Propagation loss between tracks (interpolated)",
                "Propagation loss between two tracks", timeProvider, context);

        res.add(newC);
      }

      if (getATests().allEqualLengthOrSingleton(collatedTracks))
      {
        final ICommand<IStoreItem> newC =
            new ProplossBetweenOperation(collatedTracks, destination,
                "Propagation loss between tracks (indexed)",
                "Propagation loss between two tracks", null, context);

        res.add(newC);
      }
    }
    return res;
  }
}
