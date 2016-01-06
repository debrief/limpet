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

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.GeometryBuilder;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.coordinate.Position;

public class GeoSupport
{
	public static GeometryBuilder getBuilder()
	{
		return new GeometryBuilder(DefaultGeographicCRS.WGS84);
	}

	public static GeodeticCalculator getCalculator()
	{
		return new GeodeticCalculator(DefaultGeographicCRS.WGS84);
	}

	public static Position createPosition()
	{
		return new DirectPosition2D(-4, 55.8);
	}
}
