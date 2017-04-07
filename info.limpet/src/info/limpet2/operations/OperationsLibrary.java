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
package info.limpet2.operations;

import static javax.measure.unit.NonSI.MINUTE;
import static javax.measure.unit.NonSI.NAUTICAL_MILE;
import static javax.measure.unit.SI.CELSIUS;
import static javax.measure.unit.SI.HERTZ;
import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.METRES_PER_SECOND;
import static javax.measure.unit.SI.METRES_PER_SQUARE_SECOND;
import static javax.measure.unit.SI.RADIAN;
import static javax.measure.unit.SI.SECOND;
import info.limpet.data.impl.samples.StockTypes;
import info.limpet2.IOperation;
import info.limpet2.IStoreItem;
import info.limpet2.SampleData;
import info.limpet2.operations.admin.AddLayerOperation;
import info.limpet2.operations.admin.CreateSingletonGenerator;
import info.limpet2.operations.admin.DeleteCollectionOperation;
import info.limpet2.operations.admin.GenerateDummyDataOperation;
import info.limpet2.operations.arithmetic.UnaryQuantityOperation;
import info.limpet2.operations.arithmetic.simple.AddQuantityOperation;
import info.limpet2.operations.arithmetic.simple.MultiplyQuantityOperation;
import info.limpet2.operations.arithmetic.simple.SubtractQuantityOperation;
import info.limpet2.operations.arithmetic.simple.UnitConversionOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Velocity;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.Maths;

public class OperationsLibrary
{
  /** use protected constructor to prevent accidental declaration
   * 
   */
  protected OperationsLibrary()
  {
    
  }
  
	public static final String SPATIAL = "Spatial";
	public static final String ADMINISTRATION = "Administration";
	public static final String CONVERSIONS = "Conversions";
	public static final String ARITHMETIC = "Arithmetic";
	public static final String CREATE = "Create";

	public static HashMap<String, List<IOperation>> getOperations()
	{
		HashMap<String, List<IOperation>> res = new HashMap<String, List<IOperation>>();

		res.put(ARITHMETIC, getArithmetic());
		res.put(CONVERSIONS, getConversions());
		res.put(ADMINISTRATION, getAdmin());
		res.put(SPATIAL, getSpatial());
		res.put(CREATE, getCreate());
		return res;
	}

	public static List<IOperation> getTopLevel()
	{
		List<IOperation> topLevel = new ArrayList<IOperation>();
		topLevel.add(new DeleteCollectionOperation());
		return topLevel;
	}

	private static List<IOperation> getAdmin()
	{
		List<IOperation> admin = new ArrayList<IOperation>();
		admin.add(new GenerateDummyDataOperation("small", 20));
		admin.add(new GenerateDummyDataOperation("large", 1000));
		admin.add(new GenerateDummyDataOperation("monster", 1000000));
		admin.add(new UnaryQuantityOperation("Clear units")
		{
      @Override
      protected boolean appliesTo(List<IStoreItem> selection)
      {
        return true;
      }

      @Override
      protected Unit<?> getUnaryOutputUnit(Unit<?> first)
      {
        return Dimensionless.UNIT;
      }

      @Override
      protected String getUnaryNameFor(String name)
      {
        return "Clear units";
      }

      @Override
      public Dataset calculate(Dataset input)
      {
        // TODO Auto-generated method stub
        return null;
      }
		});

		// and the export operations
//		admin.add(new ExportCsvToFileAction());
//		admin.add(new CopyCsvToClipboardAction());

		return admin;
	}

	private static List<IOperation> getArithmetic()
	{
		List<IOperation> arithmetic = new ArrayList<IOperation>();
		arithmetic.add(new MultiplyQuantityOperation());
		arithmetic.add(new AddQuantityOperation());
		arithmetic.add(new SubtractQuantityOperation());
//		arithmetic.add(new DivideQuantityOperation());
//		arithmetic.add(new SimpleMovingAverageOperation(3));

		// also our generic maths operators
		arithmetic.add(new UnaryQuantityOperation("Abs")
		{
      @Override
      protected boolean appliesTo(List<IStoreItem> selection)
      {
        return getATests().allQuantity(selection);
      }

      @Override
      protected Unit<?> getUnaryOutputUnit(Unit<?> first)
      {
        return first;
      }

      @Override
      public Dataset calculate(Dataset input)
      {
        return Maths.abs(input);
      }
		});
		arithmetic.add(new UnaryQuantityOperation("Sin")
		{
      @Override
      protected boolean appliesTo(List<IStoreItem> selection)
      {
        return getATests().allQuantity(selection);
      }

      @Override
      protected Unit<?> getUnaryOutputUnit(Unit<?> first)
      {
        return first;
      }

      @Override
      public Dataset calculate(Dataset input)
      {
        return Maths.sin(input);
      }
		});
		arithmetic.add(new UnaryQuantityOperation("Cos")
		{
		  @Override
      protected boolean appliesTo(List<IStoreItem> selection)
      {
        return getATests().allQuantity(selection);
      }

      @Override
      protected Unit<?> getUnaryOutputUnit(Unit<?> first)
      {
        return first;
      }

      @Override
      public Dataset calculate(Dataset input)
      {
        return Maths.cos(input);
      }
		});
		arithmetic.add(new UnaryQuantityOperation("Tan")
		{
		  @Override
      protected boolean appliesTo(List<IStoreItem> selection)
      {
        return getATests().allQuantity(selection);
      }

      @Override
      protected Unit<?> getUnaryOutputUnit(Unit<?> first)
      {
        return first;
      }

      @Override
      public Dataset calculate(Dataset input)
      {
        return Maths.tan(input);
      }
		});
		arithmetic.add(new UnaryQuantityOperation("Inv")
		{
		  @Override
      protected boolean appliesTo(List<IStoreItem> selection)
      {
        return getATests().allQuantity(selection);
      }

      @Override
      protected Unit<?> getUnaryOutputUnit(Unit<?> first)
      {
        return first;
      }

      @Override
      public Dataset calculate(Dataset input)
      {
        return Maths.divide(1, input);
      }
		});
		arithmetic.add(new UnaryQuantityOperation("Sqrt")
		{
		  @Override
      protected boolean appliesTo(List<IStoreItem> selection)
      {
        return getATests().allQuantity(selection);
      }

      @Override
      protected Unit<?> getUnaryOutputUnit(Unit<?> first)
      {
        return first;
      }

      @Override
      public Dataset calculate(Dataset input)
      {
        return Maths.sqrt(input);
      }
		});
		arithmetic.add(new UnaryQuantityOperation("Sqr")
		{
		  @Override
      protected boolean appliesTo(List<IStoreItem> selection)
      {
        return getATests().allQuantity(selection);
      }

      @Override
      protected Unit<?> getUnaryOutputUnit(Unit<?> first)
      {
        return first;
      }

      @Override
      public Dataset calculate(Dataset input)
      {
        return Maths.square(input);
      }
		});
		arithmetic.add(new UnaryQuantityOperation("Log")
		{
		  @Override
      protected boolean appliesTo(List<IStoreItem> selection)
      {
        return getATests().allQuantity(selection);
      }

      @Override
      protected Unit<?> getUnaryOutputUnit(Unit<?> first)
      {
        return first;
      }

      @Override
      public Dataset calculate(Dataset input)
      {
        return Maths.log(input);
      }
		});

		return arithmetic;
	}

	private static List<IOperation> getSpatial()
	{
		List<IOperation> spatial = new ArrayList<IOperation>();
//		spatial.add(new DistanceBetweenTracksOperation());
//		spatial.add(new BearingBetweenTracksOperation());
//		spatial.add(new GenerateCourseAndSpeedOperation());
//		spatial.add(new DopplerShiftBetweenTracksOperation());
//		spatial.add(new ProplossBetweenTwoTracksOperation());
		return spatial;
	}

	private static List<IOperation> getConversions()
	{
		List<IOperation> conversions = new ArrayList<IOperation>();

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

	private static List<IOperation> getCreate()
	{
		List<IOperation> create = new ArrayList<IOperation>();

		create.add(new AddLayerOperation());

		create.add(new CreateSingletonGenerator("dimensionless", Dimensionless.UNIT));

		create.add(new CreateSingletonGenerator("frequency", HERTZ.asType(Frequency.class)));

		create.add(new CreateSingletonGenerator("decibels", NonSI.DECIBEL));

		create.add(new CreateSingletonGenerator("speed (m/s)", METRE.divide(SECOND).asType(Velocity.class)));

		create.add(new CreateSingletonGenerator("course (degs)", SampleData.DEGREE_ANGLE.asType(Angle.class)));
//		create.add(new CreateLocationAction());

		return create;
	}
}
