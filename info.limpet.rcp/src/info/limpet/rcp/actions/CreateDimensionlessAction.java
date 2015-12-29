package info.limpet.rcp.actions;

import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.samples.StockTypes;

public class CreateDimensionlessAction extends CreateSingletonGenerator
{
	@Override
	protected String getName()
	{
		return "dimensionless";
	}

	@Override
	protected QuantityCollection<?> generate(String name)
	{
		return new StockTypes.NonTemporal.DimensionlessDouble(name);
	}
	
}
