/*******************************************************************************
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
 *******************************************************************************/
package info.limpet.actions;

import java.util.List;

import org.opengis.geometry.Geometry;

import info.limpet.IContext;
import info.limpet.IContext.Status;
import info.limpet.IStore;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.impl.samples.StockTypes.NonTemporal;
import info.limpet.data.operations.spatial.GeoSupport;
import info.limpet.data.store.IGroupWrapper;

public class CreateLocationAction extends AbstractLimpetAction
{

	public CreateLocationAction(IContext context)
	{
		super(context);
		setText("Create single location");
		setImageName("icons/variable.png");
	}

	@Override
	public void run()
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

			Geometry newLoc = GeoSupport.getBuilder().createPoint(dblLong, dblLat);
			newData.add(newLoc);

			// put the new collection in to the selected folder, or into root
			List<IStoreItem> selection = getSelection();
			if (selection != null && selection.size() > 0
					&& selection.get(0) instanceof IGroupWrapper)
			{
				IGroupWrapper gW = (IGroupWrapper) selection.get(0);
				gW.getGroup().add(newData);
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
			getContext().logError(Status.WARNING, "Failed to parse initial value", e);
			return;
		}
	}

}
