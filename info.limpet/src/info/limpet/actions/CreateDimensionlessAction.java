package info.limpet.actions;

import info.limpet.IContext;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.samples.StockTypes;

public class CreateDimensionlessAction extends CreateSingletonGenerator
{
	public CreateDimensionlessAction(IContext context)
	{
		super(context);
	}

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
