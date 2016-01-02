package info.limpet.actions;

import info.limpet.IContext;

public class RefreshViewAction extends AbstractLimpetAction
{

	public RefreshViewAction(IContext context)
	{
		super(context);
		setText("Refresh");
		setImageDescriptor(getContext().getImageDescriptor(IContext.REFRESH_VIEW));
	}

	@Override
	public void run()
	{
		getContext().refresh();
	}

}
