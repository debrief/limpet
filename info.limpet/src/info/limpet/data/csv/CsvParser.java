package info.limpet.data.csv;

import info.limpet.ICollection;
import info.limpet.IStore.IStoreItem;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.data.impl.samples.StockTypes.Temporal;
import info.limpet.data.impl.samples.StockTypes.Temporal.ElapsedTime_Sec;
import info.limpet.data.impl.samples.StockTypes.Temporal.Frequency_Hz;
import info.limpet.data.impl.samples.StockTypes.Temporal.Location;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.opengis.geometry.Geometry;

public class CsvParser
{
	// 21/09/2015 07:00:31
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"dd/MM/yyyy hh:mm:ss");

	public List<IStoreItem> parse(String fileName) throws IOException
	{
		List<IStoreItem> items = new ArrayList<IStoreItem>();
		Reader in = new FileReader(fileName);
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
		boolean first = true;

		// Time,Lat(Degs),Long(Degs),Elev (m),Course (Degs),Speed (kts),Temp
		// (C),Turn rate (Degs/sec)

		TemporalSeriesSupporter<?> TurnRateImporter = new TemporalSeriesSupporter<Temporal.TurnRate>(
				null, "Degs/sec")
		{
			@Override
			public Temporal.TurnRate create(String name)
			{
				return new Temporal.TurnRate(name);
			}
		};

		TemporalSeriesSupporter<?> TemperatureImporter = new TemporalSeriesSupporter<Temporal.Temp_C>(
				null, "C")
		{
			@Override
			public Temporal.Temp_C create(String name)
			{
				return new Temporal.Temp_C(name);
			}
		};
		TemporalSeriesSupporter<?> SpeedImporter = new TemporalSeriesSupporter<Temporal.Speed_MSec>(
				null, "kts")
		{
			@Override
			public Temporal.Speed_MSec create(String name)
			{
				return new Temporal.Speed_MSec(name);
			}
		};
		TemporalSeriesSupporter<?> CourseImporter = new TemporalSeriesSupporter<Temporal.Angle_Degrees>(
				null, "Degs")
		{
			@Override
			public Temporal.Angle_Degrees create(String name)
			{
				return new Temporal.Angle_Degrees(name);
			}
		};
		TemporalSeriesSupporter<?> ElapsedImporter = new TemporalSeriesSupporter<Temporal.ElapsedTime_Sec>(
				null, "secs")
		{
			@Override
			public ElapsedTime_Sec create(String name)
			{
				return new ElapsedTime_Sec(name);
			}
		};
		TemporalSeriesSupporter<?> LengthImporter = new TemporalSeriesSupporter<Temporal.Length_M>(
				null, "m")
		{
			@Override
			public Temporal.Length_M create(String name)
			{
				return new Temporal.Length_M(name);
			}
		};
		TemporalSeriesSupporter<?> FrequencyImporter = new TemporalSeriesSupporter<Temporal.Frequency_Hz>(
				null, "Hz")
		{
			@Override
			public Frequency_Hz create(String name)
			{
				return new Frequency_Hz(name);
			}
		};

		List<DataImporter> candidates = new ArrayList<DataImporter>();
		candidates.add(new LocationImporter());
		candidates.add(ElapsedImporter);
		candidates.add(FrequencyImporter);
		candidates.add(TurnRateImporter);
		candidates.add(LengthImporter);
		candidates.add(TemperatureImporter);
		candidates.add(SpeedImporter);
		candidates.add(CourseImporter);

		List<DataImporter> importers = new ArrayList<DataImporter>();
		List<ICollection> series = new ArrayList<ICollection>();

		boolean isTime = false;

		for (CSVRecord record : records)
		{
			if (first)
			{
				first = false;
				String time = record.get(0);

				if (time != null && time.toLowerCase().equals("time"))
				{
					isTime = true;
				}

				int ctr = 1;
				while (ctr < record.size())
				{
					String nextVal = record.get(ctr);
					// have a look at it.
					int i1 = nextVal.indexOf("(");

					String colName;

					if (i1 > 0)
					{
						// ok, we have units
						colName = nextVal.substring(0, i1);
					}
					else
					{
						// no, no units
						colName = nextVal;
					}

					// see if anybody can handle this name
					boolean handled = false;
					Iterator<DataImporter> cIter = candidates.iterator();
					while (cIter.hasNext())
					{
						DataImporter thisI = cIter.next();
						if (thisI.handleName(colName))
						{
							importers.add(thisI);
							series.add(thisI.create(thisI.nameFor(colName)));
							handled = true;
							ctr += thisI.numCols();
							break;
						}
					}

					if (!handled)
					{
						// String units = nextVal.substring(0, i1 - 1);

						int i2 = nextVal.indexOf(")");
						if (i2 > 0 && i2 > i1 + 1)
						{
							final String units = nextVal.substring(i1 + 1, i2);

							Iterator<DataImporter> cIter2 = candidates.iterator();
							while (cIter2.hasNext())
							{
								DataImporter thisI = cIter2.next();
								if (thisI.handleUnits(units))
								{
									importers.add(thisI);
									series.add(thisI.create(colName));
									ctr += thisI.numCols();
									break;
								}
							}
						}
					}
				}
			}
			else
			{

				String firstRow = record.get(0);
				long theTime = -1;

				// ok, we're out of the first row
				if (isTime)
				{
					// ok, get the time field
					try
					{
						Date date = DATE_FORMAT.parse(firstRow);
						theTime = date.getTime();
					}
					catch (ParseException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					// not temporal, use this field
				}

				// now move through the other cols
				int thisCol = 1;
				int numImporters = importers.size();
				for (int i = 0; i < numImporters; i++)
				{
					DataImporter thisI = importers.get(i);
					ICollection thisS = series.get(i);

					thisI.consume(thisS, theTime, thisCol, record);

					thisCol += thisI.numCols();
				}
			}
		}

		// ok, store the series
		Iterator<ICollection> sIter = series.iterator();
		while (sIter.hasNext())
		{
			ICollection coll = (ICollection) sIter.next();
			items.add(coll);

		}

		return items;
	}

	public static abstract class DataImporter
	{
		final private String _units;
		final private String _colName;

		protected DataImporter(String colName, String units)
		{
			_units = units;
			_colName = colName;
		}

		abstract public ICollection create(String name);

		public String nameFor(String colName)
		{
			return colName;
		}

		abstract public void consume(ICollection series, long thisTime,
				int colStart, CSVRecord row);

		final public boolean handleName(String colName)
		{
			if (_colName == null)
				return false;
			else
				return _colName.equals(colName);
		}

		final public boolean handleUnits(String units)
		{
			if (_units == null)
				return false;
			else
				return _units.equals(units);
		}

		public int numCols()
		{
			return 1;
		}
	}

	protected static class LocationImporter extends DataImporter
	{
		protected LocationImporter()
		{
			super("Lat", null);
		}

		public String nameFor(String colName)
		{
			return "Location";
		}

		public Temporal.Location create(String name)
		{
			return new Temporal.Location(name);
		}

		@SuppressWarnings("unused")
		public void consume(ICollection series, long thisTime, int colStart,
				CSVRecord row)
		{
			String latVal = row.get(colStart);
			Double valLat = Double.parseDouble(latVal);
			String longVal = row.get(colStart + 1);
			Double valLong = Double.parseDouble(longVal);

			Temporal.Location locS = (Location) series;
			Geometry newLoc = null;
			locS.add(thisTime, newLoc);
		}

		public int numCols()
		{
			return 2;
		}
	}

	abstract protected static class TemporalSeriesSupporter<T extends ITemporalQuantityCollection<?>>
			extends DataImporter
	{
		protected TemporalSeriesSupporter(String colName, String units)
		{
			super(colName, units);
		}

		abstract public T create(String name);

		protected void add(ICollection series, long time, Number quantity)
		{
			@SuppressWarnings("unchecked")
			T target = (T) series;
			target.add(time, quantity);
		}

		public void consume(ICollection series, long thisTime, int colStart,
				CSVRecord row)
		{
			String thisVal = row.get(colStart);
			Double val = Double.parseDouble(thisVal);
			add(series, thisTime, val);
		}

		public int numCols()
		{
			return 1;
		}
	}

}
