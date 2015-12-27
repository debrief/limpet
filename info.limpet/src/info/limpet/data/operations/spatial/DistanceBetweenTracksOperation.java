/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package info.limpet.data.operations.spatial;

import info.limpet.IBaseTemporalCollection;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IQuantityCollection;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.impl.samples.StockTypes.NonTemporal.Length_M;
import info.limpet.data.impl.samples.StockTypes.Temporal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import org.geotools.referencing.GeodeticCalculator;
import org.opengis.geometry.primitive.Point;

public class DistanceBetweenTracksOperation extends TwoTrackOperation
{

	private final class DistanceBetweenOperation extends DistanceOperation
	{

		private DistanceBetweenOperation(List<IStoreItem> selection,
				IStore store, String title, String description,
				IContext context)
		{
			this(selection, store, title, description, null, context);
		}

		public DistanceBetweenOperation(List<IStoreItem> selection,
				IStore store, String title, String description,
				IBaseTemporalCollection timeProvider, IContext context)
		{
			super(selection, store, title, description, timeProvider, context);
		}

		protected IQuantityCollection<?> getOutputCollection(String title,
				boolean isTemporal)
		{
			final IQuantityCollection<?> res;
			if (isTemporal)
			{
				res = new StockTypes.Temporal.Length_M(title,
						this);
			}
			else
			{
				res = new StockTypes.NonTemporal.Length_M(title,
						this);

			}

			return res;

		}

		protected void calcAndStore(final GeodeticCalculator calc,
				final Point locA, final Point locB, Long time)
		{
			final Unit<Length> outUnits;
			if (time != null)
			{
				// get the output dataset
				Temporal.Length_M target2 = (Temporal.Length_M) getOutputs().get(0);
				outUnits = target2.getUnits();
			}
			else
			{
				// get the output dataset
				Length_M target2 = (Length_M) getOutputs().get(0);
				outUnits = target2.getUnits();
			}

			// now find the range between them
			calc.setStartingGeographicPoint(locA.getCentroid().getOrdinate(0), locA
					.getCentroid().getOrdinate(1));
			calc.setDestinationGeographicPoint(locB.getCentroid().getOrdinate(0),
					locB.getCentroid().getOrdinate(1));
			double thisDist = calc.getOrthodromicDistance();
			final Measure<Double, Length> thisRes = Measure.valueOf(thisDist,
					outUnits);

			if (time != null)
			{
				// get the output dataset
				Temporal.Length_M target2 = (Temporal.Length_M) getOutputs().get(0);
				target2.add(time, thisRes);
			}
			else
			{
				// get the output dataset
				Length_M target2 = (Length_M) getOutputs().get(0);
				target2.add(thisRes);
			}
		}

		@Override
		protected String getOutputName()
		{
			return getContext().getInput("Distance between tracks",
					NEW_DATASET_MESSAGE,
					"Distance between " + super.getSubjectList());
		}
	}

	public Collection<ICommand<IStoreItem>> actionsFor(
			List<IStoreItem> selection, IStore destination, IContext context)
	{
		Collection<ICommand<IStoreItem>> res = new ArrayList<ICommand<IStoreItem>>();
		if (appliesTo(selection))
		{
			// ok, are we doing a tempoarl opeartion?
			if (aTests.suitableForTimeInterpolation(selection))
			{
				// hmm, find the time provider
				final IBaseTemporalCollection timeProvider = aTests
						.getLongestTemporalCollections(selection);

				// ok, provide an interpolated action
				ICommand<IStoreItem> newC = new DistanceBetweenOperation(selection,
						destination, "Distance between tracks (interpolated)", "Calculate distance between two tracks (interpolated)",
						timeProvider,
						context);
				res.add(newC);
			}

			if (aTests.allEqualLengthOrSingleton(selection))
			{
				// ok, provide an indexed action
				ICommand<IStoreItem> newC = new DistanceBetweenOperation(selection,
						destination, "Distance between tracks (indexed)", "Calculate distance between two tracks (indexed)",
						context);
				res.add(newC);
			}
		}

		return res;
	}
}
