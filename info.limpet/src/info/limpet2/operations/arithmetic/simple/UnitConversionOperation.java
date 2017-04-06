package info.limpet2.operations.arithmetic.simple;

import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.Dataset;

import info.limpet.IQuantityCollection;
import info.limpet2.Document;
import info.limpet2.NumberDocument;
import info.limpet2.operations.arithmetic.UnaryQuantityOperation;

public class UnitConversionOperation extends UnaryQuantityOperation
{

  final private Unit<?> targetUnit;

  public UnitConversionOperation(Unit<?> newUnit)
  {
    super("Convert units to " + newUnit.toString());
    targetUnit = newUnit;
  }

  @Override
  protected boolean appliesTo(List<Document> selection)
  {
    boolean singleSeries = selection.size() == 1;
    boolean allQuantity = aTests.allQuantity(selection);
    boolean sameDimension = false;
    boolean sameUnits = true;
    if (selection.size() > 0 && allQuantity)
    {
      Unit<?> units = ((NumberDocument) selection.get(0)).getUnits();
      sameDimension = units.getDimension().equals(targetUnit.getDimension());

      // check they're different units. It's not worth offering the
      // operation
      // if
      // they're already in the same units
      sameUnits = units.equals(targetUnit);
    }
    return singleSeries && allQuantity && sameDimension && !sameUnits;    
  }

  @Override
  protected Unit<?> getUnaryOutputUnit(Unit<?> first)
  {
    return targetUnit;
  }

  @Override
  protected String getUnaryNameFor(String name)
  {
    return name + " converted to:" + targetUnit.toString();
  }

  @Override
  public Dataset calculate(Dataset input)
  {
    // TODO: once UoM is in dataset, store it there
    return input;
  }

}