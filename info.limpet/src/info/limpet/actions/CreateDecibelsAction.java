package info.limpet.actions;

import info.limpet.IContext;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.samples.StockTypes;

public class CreateDecibelsAction extends CreateSingletonGenerator
{
	public CreateDecibelsAction(IContext context)
	{
		super(context);
	}

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
