package info.limpet.analysis;


abstract public class CoreAnalysis implements IAnalysis
{
	private final String _name;

	public CoreAnalysis(String name)
	{
		_name = name;
	}

	@Override
	public String getName()
	{
		return _name;
	}

}
