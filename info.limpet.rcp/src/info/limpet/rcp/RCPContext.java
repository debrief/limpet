package info.limpet.rcp;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import info.limpet.IContext;

public class RCPContext implements IContext
{

	@Override
	public String getInput(String title, String description, String defaultText)
	{
		InputDialog dlgName = new InputDialog(Display.getCurrent()
				.getActiveShell(), title, description, defaultText,
				null);
		if (dlgName.open() == Window.OK)
		{
			// User clicked OK; update the label with the input
			return  dlgName.getValue();
		}
		else
		{
			return null;
		}

	}

}
