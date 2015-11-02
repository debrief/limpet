package info.limpet.data.csv;

import info.limpet.ICollection;
import info.limpet.IQuantityCollection;
import info.limpet.IStore.IStoreItem;
import info.limpet.ITemporalQuantityCollection;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.impl.samples.StockTypes.NonTemporal;
import info.limpet.data.impl.samples.StockTypes.Temporal;
import info.limpet.data.impl.samples.StockTypes.Temporal.Location;
import info.limpet.data.impl.samples.StockTypes.Temporal.Strings;
import info.limpet.data.store.InMemoryStore.StoreGroup;

import java.io.File;
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
import org.geotools.geometry.GeometryBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.primitive.Point;

public class CsvParser
{
	// 21/09/2015 07:00:31
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"dd/MM/yyyy hh:mm:ss");
	public static final DateFormat TIME_FORMAT = new SimpleDateFormat(
			"hh:mm:ss");
	private ArrayList<DataImporter> _candidates;

	public List<IStoreItem> parse(String filePath) throws IOException
	{
		final List<IStoreItem> res = new ArrayList<IStoreItem>();
		final File inFile = new File(filePath);
		final Reader in = new FileReader(inFile);
		final String fullFileName = inFile.getName();
		final String fileName = filePrefix(fullFileName);
		final Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
		boolean first = true;

		// generate our list of importers
		createImporters();

		final DataImporter temporalDimensionless = new TemporalSeriesSupporter<Temporal.DimensionlessDouble>(
				Temporal.DimensionlessDouble.class, null, null);
		final DataImporter temporalStrings = new TemporalStringImporter();
		final DataImporter strings = new StringImporter();
		final DataImporter dimensionless = new SeriesSupporter<NonTemporal.DimensionlessDouble>(
				NonTemporal.DimensionlessDouble.class, null, null);

		// store one importer per column-set
		List<DataImporter> importers = new ArrayList<DataImporter>();

		// and store one series per column-set
		List<ICollection> series = new ArrayList<ICollection>();

		boolean isTime = false;

		for (CSVRecord record : records)
		{
			if (first)
			{
				first = false;
				String time = record.get(0);
				int ctr = 0;;

				if (time != null && time.toLowerCase().equals("time"))
				{
					isTime = true;
					ctr = 1;
				}
				else
				{
					ctr = 0;
				}

				while (ctr < record.size())
				{
					String nextVal = record.get(ctr);
					// have a look at it.
					int i1 = nextVal.indexOf("(");

					String colName;

					if (i1 > 0)
					{
						// ok, we have units
						colName = nextVal.substring(0, i1).trim();
					}
					else
					{
						// no, no units
						colName = nextVal.trim();
					}

					// see if anybody can handle this name
					boolean handled = false;
					Iterator<DataImporter> cIter = _candidates.iterator();
					while (cIter.hasNext())
					{
						DataImporter thisI = cIter.next();
						if (thisI.handleName(colName))
						{
							importers.add(thisI);
							series
									.add(thisI.create(fileName + "-" + thisI.nameFor(colName)));
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
							final String units = nextVal.substring(i1 + 1, i2).trim();

							Iterator<DataImporter> cIter2 = _candidates.iterator();
							while (cIter2.hasNext())
							{
								DataImporter thisI = cIter2.next();
								if (thisI.handleUnits(units))
								{
									importers.add(thisI);
									series.add(thisI.create(fileName + "-" + thisI.nameFor(colName)));
									ctr += thisI.numCols();
									handled = true;
									break;
								}
							}
						}
					}

					// have we managed it?
					if (!handled)
					{
						// ok, in that case we don't know. Let's introduce a deferred
						// decision
						// maker, so we can make a decision once we've read in some data
						importers.add(new DeferredLoadSupporter(colName));
						series.add(new ObjectCollection<String>("null"));
						ctr += 1;
					}
				}
			}
			else
			{

				String firstRow = record.get(0);
				long theTime = -1;
				int thisCol = 0;

				// ok, we're out of the first row
				if (isTime)
				{
					// ok, get the time field
					try
					{
						int len = firstRow.length();
						final DateFormat format;
						if(len < 10)
						{
							format = TIME_FORMAT;
						}
						else
						{
							format = DATE_FORMAT;
						}
						Date date = format.parse(firstRow);
						theTime = date.getTime();
						thisCol = 1;
					}
					catch (ParseException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					// not temporal, use this field
					thisCol = 0;
				}

				// now move through the other cols
				int numImporters = importers.size();
				for (int i = 0; i < numImporters; i++)
				{
					DataImporter thisI = importers.get(i);

					// ok, just check if this is a deferred importer
					if (thisI instanceof DeferredLoadSupporter)
					{
						DeferredLoadSupporter dl = (DeferredLoadSupporter) thisI;
						String seriesName = dl.getName();

						// ok, have a look at the next field
						String nextVal = record.get(thisCol);

						// is it numeric?
						DataImporter importer = null;
						// ok, treat it as string data
						if (isTime)
						{
							if (isNumeric(nextVal))
							{
								// ok, we've got dimensionless quantity data
								importer = temporalDimensionless;
							}
							else
							{
								importer = temporalStrings;
							}
						}
						else
						{
							if (isNumeric(nextVal))
							{
								// ok, we've got dimensionless quantity data
								importer = dimensionless;
							}
							else
							{
								importer = strings;
							}
						}
						
						if(importer != null)
						{
							int index =importers.indexOf(dl);
							importers.set(index, importer);
							
							series.set(index, importer.create(fileName + "-" + seriesName));
							
							thisI = importer;
						}
						
					}

					ICollection thisS = series.get(i);

					thisI.consume(thisS, theTime, thisCol, record);

					thisCol += thisI.numCols();
				}
			}
		}

		// ok, store the series
		if (series.size() > 1)
		{
			StoreGroup target = new StoreGroup(fullFileName);
			Iterator<ICollection> sIter = series.iterator();
			while (sIter.hasNext())
			{
				ICollection coll = (ICollection) sIter.next();
				target.add(coll);
			}
			res.add(target);
		}
		else
		{
			Iterator<ICollection> sIter = series.iterator();
			while (sIter.hasNext())
			{
				ICollection coll = (ICollection) sIter.next();
				res.add(coll);
			}
		}

		return res;
	}
	
  private String filePrefix(String fullPath) { // gets filename without extension
  	return fullPath.split("\\.(?=[^\\.]+$)")[0];

  }

	private void createImporters()
	{
		if(_candidates != null)
			return; 
		
		_candidates = new ArrayList<DataImporter>();
		_candidates.add(new LocationImporter());
		_candidates.add(new TemporalSeriesSupporter<Temporal.ElapsedTime_Sec>(
				Temporal.ElapsedTime_Sec.class, null, "secs"));
		_candidates.add(new TemporalSeriesSupporter<Temporal.Frequency_Hz>(
				Temporal.Frequency_Hz.class, null, "Hz"));
		_candidates.add(new TemporalSeriesSupporter<Temporal.TurnRate>(
				Temporal.TurnRate.class, null, "Degs/sec"));
		_candidates.add(new TemporalSeriesSupporter<Temporal.Length_M>(
				Temporal.Length_M.class, null, "m"));
		_candidates.add(new TemporalSeriesSupporter<Temporal.Angle_Degrees>(
				Temporal.Angle_Degrees.class, null, "Degs"));
		_candidates.add(new TemporalSeriesSupporter<Temporal.Speed_MSec>(
				Temporal.Speed_MSec.class, null, "kts"));
		_candidates.add(new TemporalSeriesSupporter<Temporal.Speed_MSec>(
				Temporal.Speed_MSec.class, null, "M/Sec"));
		_candidates.add(new TemporalSeriesSupporter<Temporal.Temp_C>(
				Temporal.Temp_C.class, null, "C"));
	}

	public static boolean isNumeric(String str)
	{
		try
		{
			@SuppressWarnings("unused")
			double d = Double.parseDouble(str);
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}

	/**
	 * base helper class, to help importing series of data
	 * 
	 * @author ian
	 * 
	 */
	public static abstract class DataImporter
	{
		final private String _units;
		final private String _colName;
		private Class<?> _classType;

		/**
		 * constructor
		 * 
		 * @param classType
		 *          the type of series we represent (used for default constructor)
		 * @param colName
		 *          name of the column we store
		 * @param units
		 *          name of the units we store
		 */
		protected DataImporter(Class<?> classType, String colName, String units)
		{
			_units = units;
			_colName = colName;
			_classType = classType;
		}

		/**
		 * create an instance of this series, using the specified name
		 * 
		 * @param name
		 * @return
		 */
		public ICollection create(String name)
		{
			ICollection res = null;
			try
			{
				res = (ICollection) _classType.newInstance();
				res.setName(name);
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
			return res;
		}

		/**
		 * what should this series be called, if the supplied column name is found
		 * 
		 */
		public String nameFor(String colName)
		{
			return colName;
		}

		/**
		 * read some data from this record
		 * 
		 * @param series
		 *          target series
		 * @param thisTime
		 *          this time stamp
		 * @param colStart
		 *          column to start reading from
		 * @param row
		 *          current row of data
		 */
		abstract public void consume(ICollection series, long thisTime,
				int colStart, CSVRecord row);

		/**
		 * can we handle this column name?
		 * 
		 * @param colName
		 * @return
		 */
		final public boolean handleName(String colName)
		{
			if (_colName == null)
				return false;
			else
				return _colName.equals(colName);
		}

		/**
		 * can we handle this units type?
		 * 
		 * @param units
		 * @return
		 */
		final public boolean handleUnits(String units)
		{
			if (_units == null)
				return false;
			else
				return _units.equals(units);
		}

		/**
		 * how many columns do we consume?
		 * 
		 * @return
		 */
		public int numCols()
		{
			return 1;
		}
	}

	/**
	 * class to handle importing time-related strings
	 * 
	 * @author ian
	 * 
	 */
	protected static class TemporalStringImporter extends DataImporter
	{
		protected TemporalStringImporter()
		{
			super(Temporal.Strings.class, null, null);
		}

		@Override
		public void consume(ICollection series, long thisTime, int colStart,
				CSVRecord row)
		{
			String thisVal = row.get(colStart);
			Temporal.Strings thisS = (Strings) series;
			thisS.add(thisTime, thisVal);
		}
	}

	/**
	 * class to handle importing time-related strings
	 * 
	 * @author ian
	 * 
	 */
	protected static class StringImporter extends DataImporter
	{
		protected StringImporter()
		{
			super(NonTemporal.Strings.class, null, null);
		}

		@Override
		public void consume(ICollection series, long thisTime, int colStart,
				CSVRecord row)
		{
			String thisVal = row.get(colStart);
			NonTemporal.Strings thisS = (NonTemporal.Strings) series;
			thisS.add(thisVal);
		}
	}

	
	/**
	 * class to handle importing two columns of location data
	 * 
	 * @author ian
	 * 
	 */
	protected static class LocationImporter extends DataImporter
	{
		protected LocationImporter()
		{
			super(Temporal.Location.class, "Lat", null);
		}

		public String nameFor(String colName)
		{
			return "Location";
		}

		public Temporal.Location create(String name)
		{
			return new Temporal.Location(name);
		}

		public void consume(ICollection series, long thisTime, int colStart,
				CSVRecord row)
		{
			final Temporal.Location locS = (Location) series;

			String latVal = row.get(colStart);
			Double valLat = Double.parseDouble(latVal);
			String longVal = row.get(colStart + 1);
			Double valLong = Double.parseDouble(longVal);

			GeometryBuilder builder = new GeometryBuilder(DefaultGeographicCRS.WGS84);
			Point point = builder.createPoint(valLong, valLat);

			locS.add(thisTime, point);
		}

		public int numCols()
		{
			return 2;
		}
	}

	
	/**
	 * generic class to handle importing series of data
	 * 
	 * @author ian
	 * 
	 * @param <T>
	 */
	protected static class SeriesSupporter<T extends IQuantityCollection<?>>
			extends DataImporter
	{
		protected SeriesSupporter(Class<?> classType, String colName,
				String units)
		{
			super(classType, colName, units);
		}

		protected void add(ICollection series, long time, Number quantity)
		{
			IQuantityCollection<?> target = (IQuantityCollection<?>) series;
			target.add(quantity);
		}

		@Override
		public void consume(ICollection series, long thisTime, int colStart,
				CSVRecord row)
		{
			String thisVal = row.get(colStart);
			Double val = Double.parseDouble(thisVal);
			add(series, thisTime, val);
		}

		@Override
		public int numCols()
		{
			return 1;
		}
	}

	/**
	 * generic class to handle importing series of data
	 * 
	 * @author ian
	 * 
	 * @param <T>
	 */
	protected static class TemporalSeriesSupporter<T extends ITemporalQuantityCollection<?>>
			extends DataImporter
	{
		protected TemporalSeriesSupporter(Class<?> classType, String colName,
				String units)
		{
			super(classType, colName, units);
		}

		protected void add(ICollection series, long time, Number quantity)
		{
			ITemporalQuantityCollection<?> target = (ITemporalQuantityCollection<?>) series;
			target.add(time, quantity);
		}

		@Override
		public void consume(ICollection series, long thisTime, int colStart,
				CSVRecord row)
		{
			String thisVal = row.get(colStart);
			Double val = Double.parseDouble(thisVal);
			add(series, thisTime, val);
		}

		@Override
		public int numCols()
		{
			return 1;
		}
	}

	protected static class DeferredLoadSupporter extends DataImporter
	{
		String _name;

		public DeferredLoadSupporter(String name)
		{
			super(null, null, null);
			_name = name;
		}

		public String getName()
		{
			return _name;
		}

		@Override
		public void consume(ICollection series, long thisTime, int colStart,
				CSVRecord row)
		{
			throw new RuntimeException(
					"We're just temporary - we should never actually be called!");
		}

	}
}
