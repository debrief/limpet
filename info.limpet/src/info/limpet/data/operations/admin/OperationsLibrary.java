package info.limpet.data.operations.admin;

import static javax.measure.unit.NonSI.MINUTE;
import static javax.measure.unit.NonSI.NAUTICAL_MILE;
import static javax.measure.unit.SI.CELSIUS;
import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.METRES_PER_SECOND;
import static javax.measure.unit.SI.METRES_PER_SQUARE_SECOND;
import static javax.measure.unit.SI.RADIAN;
import static javax.measure.unit.SI.SECOND;
import info.limpet.IOperation;
import info.limpet.IQuantityCollection;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet.data.operations.AddQuantityOperation;
import info.limpet.data.operations.CollectionComplianceTests;
import info.limpet.data.operations.DeleteCollectionOperation;
import info.limpet.data.operations.DivideQuantityOperation;
import info.limpet.data.operations.GenerateDummyDataOperation;
import info.limpet.data.operations.MultiplyQuantityOperation;
import info.limpet.data.operations.SimpleMovingAverageOperation;
import info.limpet.data.operations.SubtractQuantityOperation;
import info.limpet.data.operations.UnitConversionOperation;
import info.limpet.data.operations.UnitaryAngleOperation;
import info.limpet.data.operations.UnitaryMathOperation;
import info.limpet.data.operations.spatial.BearingBetweenTracksOperation;
import info.limpet.data.operations.spatial.DistanceBetweenTracksOperation;
import info.limpet.data.operations.spatial.DopplerShiftBetweenTracksOperation;
import info.limpet.data.operations.spatial.GenerateCourseAndSpeedOperation;
import info.limpet.data.operations.spatial.ProplossBetweenTwoTracksOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Velocity;
import javax.measure.unit.Unit;

public class OperationsLibrary
{
	public static final String SPATIAL = "Spatial";
	public static final String ADMINISTRATION = "Administration";
	public static final String CONVERSIONS = "Conversions";
	public static final String ARITHMETIC = "Arithmetic";
	final static CollectionComplianceTests aTests = new CollectionComplianceTests();

	public static HashMap<String, List<IOperation<?>>> getOperations()
	{
		HashMap<String, List<IOperation<?>>> res = new HashMap<String, List<IOperation<?>>>();

		res.put(ARITHMETIC, getArithmetic());
		res.put(CONVERSIONS, getConversions());
		res.put(ADMINISTRATION, getAdmin());
		res.put(SPATIAL, getSpatial());
		return res;
	}

	public static List<IOperation<?>> getTopLevel()
	{
		List<IOperation<?>> topLevel = new ArrayList<IOperation<?>>();
		topLevel.add(new DeleteCollectionOperation());
		return topLevel;
	}

	private static List<IOperation<?>> getAdmin()
	{
		List<IOperation<?>> admin = new ArrayList<IOperation<?>>();
		admin.add(new GenerateDummyDataOperation("small", 20));
		admin.add(new GenerateDummyDataOperation("large", 1000));
		admin.add(new GenerateDummyDataOperation("monster", 1000000));
		admin.add(new UnitaryMathOperation("Clear units")
		{
			public double calcFor(double val)
			{
				return val;
			}

			protected Unit<?> getUnits(IQuantityCollection<?> input)
			{
				return Dimensionless.UNIT;
			}
		});

		return admin;
	}

	private static List<IOperation<?>> getArithmetic()
	{
		List<IOperation<?>> arithmetic = new ArrayList<IOperation<?>>();
		arithmetic.add(new MultiplyQuantityOperation());
		arithmetic.add(new AddQuantityOperation<>());
		arithmetic.add(new SubtractQuantityOperation<>());
		arithmetic.add(new DivideQuantityOperation());
		arithmetic.add(new SimpleMovingAverageOperation(3));

		// also our generic maths operators
		arithmetic.add(new UnitaryMathOperation("Abs")
		{
			public double calcFor(double val)
			{
				return Math.abs(val);
			}
		});
		arithmetic.add(new UnitaryAngleOperation("Sin")
		{
			public double calcFor(double val)
			{
				return Math.sin(val);
			}
		});
		arithmetic.add(new UnitaryAngleOperation("Cos")
		{
			public double calcFor(double val)
			{
				return Math.sin(val);
			}
		});
		arithmetic.add(new UnitaryAngleOperation("Tan")
		{
			public double calcFor(double val)
			{
				return Math.tan(val);
			}
		});
		arithmetic.add(new UnitaryMathOperation("Inv")
		{
			public double calcFor(double val)
			{
				return -val;
			}
		});
		arithmetic.add(new UnitaryMathOperation("Sqrt")
		{
			public double calcFor(double val)
			{
				return Math.sqrt(val);
			}
		});
		arithmetic.add(new UnitaryMathOperation("Sqr")
		{
			public double calcFor(double val)
			{
				return val * val;
			}
		});
		arithmetic.add(new UnitaryMathOperation("Log")
		{
			public double calcFor(double val)
			{
				return Math.log(val);
			}
		});

		return arithmetic;
	}

	private static List<IOperation<?>> getSpatial()
	{
		List<IOperation<?>> spatial = new ArrayList<IOperation<?>>();
		spatial.add(new DistanceBetweenTracksOperation());
		spatial.add(new BearingBetweenTracksOperation());
		spatial.add(new GenerateCourseAndSpeedOperation());
		spatial.add(new DopplerShiftBetweenTracksOperation());
		spatial.add(new ProplossBetweenTwoTracksOperation());
		return spatial;
	}

	private static List<IOperation<?>> getConversions()
	{
		List<IOperation<?>> conversions = new ArrayList<IOperation<?>>();

		// Length
		conversions.add(new UnitConversionOperation(METRE));

		// Time
		conversions.add(new UnitConversionOperation(SECOND));
		conversions.add(new UnitConversionOperation(MINUTE));

		// Speed
		conversions.add(new UnitConversionOperation(METRES_PER_SECOND));
		conversions.add(new UnitConversionOperation(NAUTICAL_MILE.divide(
				SECOND.times(3600)).asType(Velocity.class)));

		// Acceleration
		conversions.add(new UnitConversionOperation(METRES_PER_SQUARE_SECOND));

		// Temperature
		conversions.add(new UnitConversionOperation(CELSIUS));

		// Angle
		conversions.add(new UnitConversionOperation(RADIAN));
		conversions.add(new UnitConversionOperation(StockTypes.DEGREE_ANGLE));
		return conversions;
	}
}
