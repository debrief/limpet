package info.limpet.data.csv;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import info.limpet.IStore.IStoreItem;
import info.limpet.data.impl.samples.StockTypes;

public class CsvParser
{
	// 21/09/2015 07:00:31
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	public List<IStoreItem> parse(String fileName) throws IOException
	{
		List<IStoreItem> items = new ArrayList<IStoreItem>();
		Reader in = new FileReader(fileName);
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
		boolean first = true;
		boolean isElapsedTime = false;
		boolean isFrequency = false;
		String name;
		String unit;
		StockTypes.Temporal.ElapsedTime_Sec interval = null;
		StockTypes.Temporal.Frequency_Hz frequency = null;
		for (CSVRecord record : records) 
		{
			if (first)
			{
				first = false;
		    String time = record.get(0);
		    if (time != null && time.toLowerCase().equals("time"))
		    {
		    	String second = record.get(1);
		    	if (second != null)
		    	{
		    		int i1 = second.indexOf("(");
		    		if (i1 > 0)
		    		{
		    			name = second.substring(0, i1-1);
		    			int i2 = second.indexOf(")");
		    			if (i2 > 0 && i2 > i1 + 1)
		    			{
		    				unit = second.substring(i1 + 1, i2);
		    				if (unit != null && unit.toLowerCase().equals("secs"))
		    				{
		    					isElapsedTime = true;
		    					interval = new StockTypes.Temporal.ElapsedTime_Sec(name);
		    					items.add(interval);
		    				}
		    				if (unit != null && unit.toLowerCase().equals("hz"))
		    				{
		    					isFrequency = true;
		    					frequency = new StockTypes.Temporal.Frequency_Hz(name);
		    					items.add(frequency);
		    				}
		    			}
		    		}
		    	}
		    }
			} 
			else
			{
				if (!isElapsedTime && !isFrequency)
				{
					throw new IOException("Invalid file");
				}
				String timeString = record.get(0);
				String valueString = record.get(1);
				Double value;
				try
				{
					value = new Double(valueString);
				}
				catch (NumberFormatException e)
				{
					throw new IOException("Invalid number " + valueString);
				}
				long timeLong;
				try
				{
					Date date = DATE_FORMAT.parse(timeString);
					timeLong = date.getTime();
				}
				catch (ParseException e)
				{
					throw new IOException("Invalid time " + timeString);
				}
				if (isElapsedTime)
				{
					interval.add(timeLong, value);
				}
				if (isFrequency)
				{
					frequency.add(timeLong, value);
				}
			}
		}
		return items;
	}

}
