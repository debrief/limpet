package info.limpet.actions;

import info.limpet.IContext;
import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.samples.StockTypes;

public class CreateCourseAction extends CreateSingletonGenerator
{
	public CreateCourseAction(IContext context)
	{
		super(context);
	}

	@Override
	protected String getName()
	{
		return "course (degs)";
	}

	@Override
	protected QuantityCollection<?> generate(String name)
	{
		return new StockTypes.NonTemporal.Angle_Degrees(name);
	}
	
}
