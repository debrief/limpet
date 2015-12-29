package info.limpet.rcp.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

public class CopyCsvToClipboardAction extends AbstractLimpetAction
{

	@Override
	public void run()
	{
		String csv = getCsvString();
		if (csv != null && !csv.isEmpty())
		{
			final Clipboard cb = new Clipboard(Display.getCurrent());
			TextTransfer textTransfer = TextTransfer.getInstance();
			cb.setContents(new Object[]
			{ csv }, new Transfer[]
			{ textTransfer });
		}
		else
		{
			MessageDialog.openInformation(getShell(), "Data Manager Editor",
					"Cannot copy current selection");
		}
	}

}
