package info.limpet.rcp.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class CopyCsvToClipboardAction extends AbstractLimpetAction
{

	public CopyCsvToClipboardAction()
	{
		setText("Copy CSV to Clipboard");
		setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
	}

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
