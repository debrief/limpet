package info.limpet.rcp.actions;

import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.samples.StockTypes;

public class CreateDecibelsAction extends CreateSingletonGenerator
{
	@Override
	protected String getName()
	{
		return "decibels";
	}

	@Override
	protected QuantityCollection<?> generate(String name)
	{
		return new StockTypes.NonTemporal.AcousticStrength(name);
	}
	
}
