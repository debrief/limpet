package info.limpet.rcp.actions;

import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.samples.StockTypes;

public class CreateSpeedAction extends CreateSingletonGenerator
{
	@Override
	protected String getName()
	{
		return "speed (m/s)";
	}

	@Override
	protected QuantityCollection<?> generate(String name)
	{
		return new StockTypes.NonTemporal.Speed_MSec(name);
	}
	
}
