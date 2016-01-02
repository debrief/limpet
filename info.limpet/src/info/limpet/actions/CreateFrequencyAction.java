package info.limpet.actions;

import info.limpet.IContext;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.samples.StockTypes;

public class CreateFrequencyAction extends CreateSingletonGenerator
{
	public CreateFrequencyAction(IContext context)
	{
		super(context);
	}

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
