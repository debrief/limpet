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
package info.limpet.data.persistence.xml;

import org.geotools.geometry.GeometryBuilder;
import org.geotools.geometry.iso.primitive.PointImpl;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.primitive.Point;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class PointConverter implements Converter
{

	private static GeometryBuilder builder = new GeometryBuilder(
			DefaultGeographicCRS.WGS84);

	public PointConverter()
	{
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean canConvert(Class type)
	{
		return PointImpl.class.isAssignableFrom(type);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context)
	{
		Point point = (Point) source;
		writer.startNode("lat");
		String value = Double
				.toString(point.getDirectPosition().getCoordinate()[0]);
		writer.setValue(value);
		writer.endNode();
		writer.startNode("lon");
		value = Double.toString(point.getDirectPosition().getCoordinate()[1]);
		writer.setValue(value);
		writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context)
	{
		double lat = 0, lon = 0;
		while (reader.hasMoreChildren())
		{
			reader.moveDown();
			if ("lat".equals(reader.getNodeName()))
			{
				lat = new Double(reader.getValue()).doubleValue();
			}
			else if ("lon".equals(reader.getNodeName()))
			{
				lon = new Double(reader.getValue()).doubleValue();
			}
			reader.moveUp();
		}
		Point point = builder.createPoint(lat, lon);
		return point;
	}

}
