package info.limpet.actions;

import info.limpet.IContext;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.samples.StockTypes;

public class CreateSpeedAction extends CreateSingletonGenerator
{
	public CreateSpeedAction(IContext context)
	{
		super(context);
	}

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
