package info.limpet.data.csv;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;

import info.limpet.ICollection;
import info.limpet.IObjectCollection;
import info.limpet.IQuantityCollection;
import info.limpet.ITemporalObjectCollection;

public class CsvGenerator
{
	private static final String RIGHT_PARENTHESES = ")";
	private static final String LEFT_PARENTHESES = "(";
	private static final String COMMA_SEPARATOR = ",";
	private static final String LINE_SEPARATOR = "\n";

	@SuppressWarnings("unchecked")
	public String generate(ICollection collection)
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
		header.append(collection.getName());
		if (collection.isQuantity())
		{
			header.append(LEFT_PARENTHESES);
			String unitSymbol = ((IQuantityCollection<Quantity>) collection)
					.getUnits().toString();
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
			else
			{
				header.append(value);
			}
			header.append(LINE_SEPARATOR);
		}
		return header.toString();
	}
}
