package info.limpet.actions;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import info.limpet.IContext;

public class CopyCsvToClipboardAction extends AbstractLimpetAction
{

	public CopyCsvToClipboardAction(IContext context)
	{
		super(context);
		setText("Copy CSV to Clipboard");
		setImageDescriptor(context.getImageDescriptor(IContext.COPY_CSV_TO_CLIPBOARD));
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
			getContext().openInformation("Data Manager Editor",
					"Cannot copy current selection");
		}
	}

}
