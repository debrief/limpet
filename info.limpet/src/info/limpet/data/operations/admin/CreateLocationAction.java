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
package info.limpet.data.operations.admin;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IContext.Status;
import info.limpet.IStore;
import info.limpet.IStoreItem;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.samples.StockTypes.NonTemporal;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.data.operations.spatial.GeoSupport;
import info.limpet.data.store.StoreGroup;

import java.awt.geom.Point2D;

public class CreateLocationAction extends CreateSingletonGenerator
{
	public CreateLocationAction()
	{
		super("location");
	}

	private final CollectionComplianceTests aTests = new CollectionComplianceTests();

	/**
	 * encapsulate creating a location into a command
	 * 
	 * @author ian
	 * 
	 */
	public static class CreateLocationCommand extends AbstractCommand<IStoreItem>
	{
		private StoreGroup _targetGroup;

		public CreateLocationCommand(String title, StoreGroup group, IStore store,
				IContext context)
		{
			super(title, "Create single location", store, false, false, null, context);
			_targetGroup = group;
		}

		@Override
		public void execute()
		{
			// get the name
			String seriesName = getContext().getInput("New fixed location",
					"Enter name for location", "");

			if (seriesName == null || seriesName.isEmpty())
			{
				return;
			}
			String strLat = getContext().getInput("New location",
					"Enter initial value for latitude", "");
			if (strLat == null || strLat.isEmpty())
			{
				return;
			}
			String strLong = getContext().getInput("New location",
					"Enter initial value for longitude", "");
			if (strLong == null || strLong.isEmpty())
			{
				return;
			}
			try
			{

				NonTemporal.Location newData = new NonTemporal.Location(seriesName);

				// add the new value
				double dblLat = Double.parseDouble(strLat);
				double dblLong = Double.parseDouble(strLong);

				Point2D newLoc = GeoSupport.getCalculator().createPoint(dblLong, dblLat);
				newData.add(newLoc);

				// put the new collection in to the selected folder, or into root
				if (_targetGroup != null)
				{
					_targetGroup.add(newData);
				}
				else
				{
					// just store it at the top level
					IStore store = getStore();
					if (store != null)
					{
						store.add(newData);
					}
				}

			}
			catch (NumberFormatException e)
			{
				getContext().logError(Status.WARNING, "Failed to parse initial value",
						e);
				return;
			}
		}

		@Override
		protected void recalculate(IStoreItem subject)
		{
			// don't worry
		}

		@Override
		protected String getOutputName()
		{
			return getContext().getInput("Create location", NEW_DATASET_MESSAGE, "");
		}

	}

	@Override
	protected AbstractCommand<IStoreItem> getCommand(IStore destination,
			IContext context, String thisTitle, StoreGroup group)
	{
		// TODO Auto-generated method stub
		return new CreateLocationCommand(thisTitle, group, destination, context);
	}

	@Override
	protected QuantityCollection<?> generate(String name, ICommand<?> precedent)
	{
		// we shouldn't be using this
		throw new RuntimeException(
				"we shouldn't call this, our command knows it's working with a location");
	}

  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

}
