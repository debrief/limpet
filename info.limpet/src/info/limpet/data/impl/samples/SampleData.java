package info.limpet.data.impl.samples;

import info.limpet.ICommand;
import info.limpet.IObjectCollection;
import info.limpet.IQuantityCollection;
import info.limpet.IStore.IStoreItem;
import info.limpet.QuantityRange;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.samples.StockTypes.Temporal.ElapsedTime_Sec;
import info.limpet.data.impl.samples.StockTypes.Temporal.Location;
import info.limpet.data.operations.AddQuantityOperation;
import info.limpet.data.operations.MultiplyQuantityOperation;
import info.limpet.data.store.InMemoryStore;
import info.limpet.data.store.InMemoryStore.StoreGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Velocity;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.GeometryBuilder;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.primitive.Point;
import org.opengis.referencing.operation.TransformException;

public class SampleData
{
	public static final String SPEED_IRREGULAR2 = "Speed two irregular time";
	public static final String TIME_INTERVALS = "Time intervals";
	public static final String STRING_TWO = "String two";
	public static final String STRING_ONE = "String one";
	public static final String LENGTH_SINGLETON = "Length Singleton";
	public static final String LENGTH_TWO = "Length Two non-Time";
	public static final String LENGTH_ONE = "Length One non-Time";
	public static final String ANGLE_ONE = "Angle One Time";
	public static final String SPEED_ONE = "Speed One Time";
	public static final String SPEED_TWO = "Speed Two Time";
	public static final String TRACK_ONE = "Track One Time";
	public static final String TRACK_TWO = "Track Two Time";
	public static final String SPEED_EARLY = "Speed Two Time (earlier)";
	public static final String RANGED_SPEED_SINGLETON = "Ranged Speed Singleton";
	public static final String FLOATING_POINT_FACTOR = "Floating point factor";

	public InMemoryStore getData(long count)
	{
		InMemoryStore res = new InMemoryStore();

		// // collate our data series
		StockTypes.Temporal.Angle_Degrees angle1 = new StockTypes.Temporal.Angle_Degrees(
				ANGLE_ONE, null);
		StockTypes.Temporal.Speed_MSec speedSeries1 = new StockTypes.Temporal.Speed_MSec(
				SPEED_ONE);
		StockTypes.Temporal.Speed_MSec speedSeries2 = new StockTypes.Temporal.Speed_MSec(
				SPEED_TWO);
		StockTypes.Temporal.Speed_MSec speedSeries3 = new StockTypes.Temporal.Speed_MSec(
				"Speed Three (longer)");
		StockTypes.Temporal.Speed_MSec speed_early_1 = new StockTypes.Temporal.Speed_MSec(
				SPEED_EARLY);
		StockTypes.Temporal.Speed_MSec speed_irregular = new StockTypes.Temporal.Speed_MSec(
				SPEED_IRREGULAR2);
		StockTypes.NonTemporal.Length_M length1 = new StockTypes.NonTemporal.Length_M(
				LENGTH_ONE);
		StockTypes.NonTemporal.Length_M length2 = new StockTypes.NonTemporal.Length_M(
				LENGTH_TWO);
		IObjectCollection<String> string1 = new ObjectCollection<String>(STRING_ONE);
		IObjectCollection<String> string2 = new ObjectCollection<String>(STRING_TWO);
		IQuantityCollection<Dimensionless> singleton1 = new QuantityCollection<Dimensionless>(
				FLOATING_POINT_FACTOR, Dimensionless.UNIT);
		StockTypes.NonTemporal.Speed_MSec singletonRange1 = new StockTypes.NonTemporal.Speed_MSec(
				RANGED_SPEED_SINGLETON);
		StockTypes.NonTemporal.Length_M singletonLength = new StockTypes.NonTemporal.Length_M(
				LENGTH_SINGLETON);
		ElapsedTime_Sec timeIntervals = new StockTypes.Temporal.ElapsedTime_Sec(
				TIME_INTERVALS);
		Location track_1 = new StockTypes.Temporal.Location(TRACK_ONE);
		Location track_2 = new StockTypes.Temporal.Location(TRACK_TWO);

		long thisTime = 0;
		
		// get ready for the track generation
		GeometryBuilder builder = new GeometryBuilder( DefaultGeographicCRS.WGS84 );
		GeodeticCalculator geoCalc = new GeodeticCalculator(DefaultGeographicCRS.WGS84);
		DirectPosition pos_1 = new DirectPosition2D(-4,  55.8);
		DirectPosition pos_2 = new DirectPosition2D(-4.2,  54.9);
		
		

		for (int i = 1; i <= count; i++)
		{
			thisTime = new Date().getTime() + i * 500 * 60;

			final long earlyTime = thisTime - (1000 * 60 * 60 * 24 * 365 * 20);

			angle1.add(thisTime,
					90 + 1.1 * Math.toDegrees(Math.sin(Math.toRadians(i * 52.5))));
			speedSeries1.add(thisTime, 1 / Math.sin(i));
			speedSeries2.add(thisTime, 7 + 2 * Math.sin(i));

			// we want the irregular series to only have occasional
			if (i % 5 == 0)
			{
				speed_irregular.add(thisTime + 500 * 45, 7 + 2 * Math.sin(i + 1));
			}
			else
			{
				if (Math.random() > 0.6)
				{
					speed_irregular.add(thisTime + 500 * 25 * 2, 7 + 2 * Math.sin(i - 1));
				}
			}

			speedSeries3.add(thisTime, 3d * Math.cos(i));
			speed_early_1.add(earlyTime, Math.sin(i));
			length1.add((double) i % 3);
			length2.add((double) i % 5);
			string1.add("item " + i);
			string2.add("item " + (i % 3));
			timeIntervals.add(thisTime, (4 + Math.sin(Math.toRadians(i) + 3.4 * Math.random())));
			
			// sort out the tracks
			try
			{
				geoCalc.setStartingGeographicPoint(pos_1.getOrdinate(0), pos_1.getOrdinate(1));
				geoCalc.setDirection(Math.toRadians(77 - (i * 4)), 554);
				pos_1 = geoCalc.getDestinationPosition();				
				Point p1 = builder.createPoint(pos_1.getOrdinate(0), pos_1.getOrdinate(1));

				
				geoCalc.setStartingGeographicPoint(pos_2.getOrdinate(0), pos_2.getOrdinate(1));
				geoCalc.setDirection(Math.toRadians(54 + (i * 5)), 133);
				pos_2 = geoCalc.getDestinationPosition();
				Point p2 = builder.createPoint(pos_2.getOrdinate(0), pos_2.getOrdinate(1));

				track_1.add(thisTime, p1);
				track_2.add(thisTime, p2);
				
			}
			catch (TransformException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
		}

		// add an extra item to speedSeries3
		speedSeries3.add(thisTime + 12 * 500 * 60, 12);

		// give the singleton a value
		singleton1.add(4d);
		singletonRange1.add(998);
		Measure<Double, Velocity> minR = Measure.valueOf(940d, singletonRange1.getUnits());
		Measure<Double, Velocity> maxR = Measure.valueOf(1050d, singletonRange1.getUnits()); 
		QuantityRange<Velocity> speedRange = new QuantityRange<Velocity>(
				minR, maxR);
		singletonRange1.setRange(speedRange);

		singletonLength.add(12d);

		List<IStoreItem> list = new ArrayList<IStoreItem>();
		
		StoreGroup group1 = new StoreGroup("Speed data");
		group1.add(speedSeries1);
		group1.add(speedSeries2);
		group1.add(speed_irregular);
		group1.add(speed_early_1);
		group1.add(speedSeries3);
		
		list.add(group1);

		list.add(angle1);
		list.add(length1);
		list.add(length2);
		list.add(string1);
		list.add(string2);
		list.add(singleton1);
		list.add(singletonRange1);
		list.add(singletonLength);
		list.add(timeIntervals);
		list.add(track_1);
		list.add(track_2);

		res.addAll(list);

		// perform an operation, so we have some audit trail
		List<IStoreItem> selection = new ArrayList<IStoreItem>();
		selection.add(speedSeries1);
		selection.add(speedSeries2);
		@SuppressWarnings(
		{ "unchecked", "rawtypes" })
		Collection<ICommand<?>> actions = new AddQuantityOperation().actionsFor(
				selection, res);
		ICommand<?> addAction = actions.iterator().next();
		addAction.execute();

		// and an operation using our speed factor
		selection.clear();
		selection.add(speedSeries1);
		selection.add(singleton1);
		Collection<ICommand<IStoreItem>> actions2 = new MultiplyQuantityOperation()
				.actionsFor(selection, res);
		addAction = actions2.iterator().next();
		addAction.execute();

		// calculate the distance travelled
		selection.clear();
		selection.add(timeIntervals);
		selection.add(singletonRange1);
		Collection<ICommand<IStoreItem>> actions3 = new MultiplyQuantityOperation(
				"Calculated distance").actionsFor(selection, res);
		addAction = actions3.iterator().next();
		addAction.execute();

		return res;
	}
}
