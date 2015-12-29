package info.limpet.rcp.actions;

import info.limpet.data.impl.QuantityCollection;
import info.limpet.data.impl.samples.StockTypes;

public class CreateCourseAction extends CreateSingletonGenerator
{
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
