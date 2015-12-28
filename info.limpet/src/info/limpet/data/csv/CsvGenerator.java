package info.limpet.data.csv;

import info.limpet.ICollection;
import info.limpet.IObjectCollection;
import info.limpet.IQuantityCollection;
import info.limpet.ITemporalObjectCollection;
import info.limpet.data.impl.samples.StockTypes.ILocations;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;

import org.opengis.geometry.primitive.Point;

public class CsvGenerator
{
	private static final String LEFT_BRACKET = "[";
	private static final String RIGHT_BRACKET = "]";
	private static final String RIGHT_PARENTHESES = ")";
	private static final String LEFT_PARENTHESES = "(";
	private static final String COMMA_SEPARATOR = ",";
	private static final String LINE_SEPARATOR = "\n";

	@SuppressWarnings("unchecked")
	public static String generate(ICollection collection)
	{
		if (!(collection instanceof IObjectCollection))
		{
			return null;
		}
		StringBuilder header = new StringBuilder();
		if (collection.isTemporal())
		{
			header.append("Time,");
		}
		if (collection instanceof ILocations)
		{
			header.append("Lat(Degs),Long(Degs)");
		}
		else 
		{
			// "(" and "(" has special meaning in CsvParser (separate unit)
			// replace with "[" and "]"
			String name = collection.getName();
			name = name.replace(LEFT_PARENTHESES, LEFT_BRACKET);
			name = name.replace(RIGHT_PARENTHESES, RIGHT_BRACKET);
			header.append(name);
			addUnit(header, collection);
		}
		header.append(LINE_SEPARATOR);

		Iterator<Long> timesIterator = null;
		if (collection.isTemporal())
		{
			List<Long> times = ((ITemporalObjectCollection<Object>) collection)
					.getTimes();
			timesIterator = times.iterator();
		}
		IObjectCollection<Object> objectCollection = (IObjectCollection<Object>) collection;
		List<Object> values = objectCollection.getValues();
		Iterator<Object> valuesIterator = values.iterator();
		while (valuesIterator.hasNext())
		{
			if (timesIterator != null && timesIterator.hasNext())
			{
				Long time = timesIterator.next();
				header.append(CsvParser.DATE_FORMAT.format(new Date(time)));
				header.append(COMMA_SEPARATOR);
			}
			Object value = valuesIterator.next();
			if (value instanceof Measure)
			{
				header.append(((Measure<?, Quantity>) value).getValue());
			}
			else if (value instanceof Point)
			{
				Point point = (Point) value;
				header.append(point.getDirectPosition().getCoordinate()[1]);
				header.append(COMMA_SEPARATOR);
				header.append(point.getDirectPosition().getCoordinate()[0]);
			}
			else
			{
				header.append(value);
			}
			header.append(LINE_SEPARATOR);
		}
		return header.toString();
	}

	private static void addUnit(StringBuilder header, ICollection collection)
	{
		if (collection.isQuantity())
		{
			header.append(LEFT_PARENTHESES);
			@SuppressWarnings("unchecked")
			String unitSymbol = ((IQuantityCollection<Quantity>) collection).getUnits().toString();
			// DEGREE_ANGLE
			if ("°".equals(unitSymbol))
			{
				header.append("Degs");
			}
			else
			{
				header.append(unitSymbol);
			}
			header.append(RIGHT_PARENTHESES);
		}
	}
}
