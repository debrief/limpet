package info.limpet.rcp.actions;

import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.samples.StockTypes;

public class CreateFrequencyCommand extends CreateSingletonGenerator
{
	@Override
	protected String getName()
	{
		return "frequency";
	}

	@Override
	protected QuantityCollection<?> generate(String name)
	{
		return new StockTypes.NonTemporal.Frequency_Hz(name);
	}
	
}
