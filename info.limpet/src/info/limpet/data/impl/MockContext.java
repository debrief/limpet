package info.limpet.data.impl;

import info.limpet.IContext;

public class MockContext implements IContext
{

	@Override
	public String getInput(String title, String description, String defaultText)
	{
		return defaultText;
	}

}
