package info.limpet.rcp.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import info.limpet.rcp.Activator;

public class CopyCsvToFileCommand extends AbstractLimpetHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		String csv = getCsvString();
		if (csv != null && !csv.isEmpty())
		{
			FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
			final String[] filterNames;
			final String[] filterExtensions;
			String filterPath = "";
			if (SWT.getPlatform().equals("win32"))
			{
				filterNames = new String[]
				{ "Csv File", "All Files (*.*)" };
				filterExtensions = new String[]
				{ "*.csv", "*.*" };
			}
			else
			{
				filterNames = new String[]
				{ "Csv File", "All Files (*)" };
				filterExtensions = new String[]
				{ "*.csv", "*" };
			}
			dialog.setFilterNames(filterNames);
			dialog.setFilterExtensions(filterExtensions);
			dialog.setFilterPath(filterPath);
			dialog.setFileName("limpet_out.csv");
			String result = dialog.open();
			if (result == null)
			{
				return null;
			}
			File file = new File(result);
			if (file.exists())
			{
				if (!MessageDialog.openQuestion(getShell(),
						"Overwrite '" + result + "'?",
						"Are you sure you want to overwrite '" + result + "'?"))
				{
					return null;
				}
			}
			FileOutputStream fop = null;
			try
			{
				fop = new FileOutputStream(file);
				fop.write(csv.getBytes());
			}
			catch (IOException e)
			{
				MessageDialog.openError(getShell(), "Error",
						"Cannot write to '" + result + "'. See log for more details");
				Activator.log(e);
			}
			finally
			{
				if (fop != null)
				{
					try
					{
						fop.close();
					}
					catch (IOException e)
					{
						Activator.logError(Status.ERROR,
								"Failed to close fop in DataManagerEditor export to CSV", e);
					}
				}
			}

		}
		else
		{
			MessageDialog.openInformation(getShell(), "Data Manager Editor",
					"Cannot copy current selection");
		}
		return null;
	}

}
