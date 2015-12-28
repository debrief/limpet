package info.limpet.data.impl;

import info.limpet.IContext;

public class MockContext implements IContext
{

	@Override
	public String getInput(String title, String description, String defaultText)
	{
		return defaultText;
	}

	@Override
	public void logError(Status status, String message, Exception e)
	{
		System.err.println("Logging status:" + status + " message:" + message);
		e.printStackTrace();
	}

}
