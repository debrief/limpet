package info.limpet.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import info.limpet.IContext;
import info.limpet.IContext.Status;

/**
 *  Provides UI & processing to export a single collection to a CSV file
 *  
 * @author ian
 *
 */
public class ExportCsvToFileAction extends AbstractLimpetAction
{

	public ExportCsvToFileAction(IContext context)
	{
		super(context);
		setText("Copy CSV to File");
		setImageDescriptor(context.getImageDescriptor(IContext.COPY_CSV_TO_FILE));
	}

	@Override
	public void run()
	{
		String csv = getCsvString();
		if (csv != null && !csv.isEmpty())
		{
			String result = getContext().getCsvFilename();
			if (result == null)
			{
				return;
			}
			File file = new File(result);
			if (file.exists())
			{
				if (!getContext().openQuestion("Overwrite '" + result + "'?",
						"Are you sure you want to overwrite '" + result + "'?"))
				{
					return;
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
				getContext().openError("Error", "Cannot write to '" + result + "'. See log for more details");
				getContext().log(e);
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
						getContext().logError(Status.ERROR,
								"Failed to close fop in DataManagerEditor export to CSV", e);
					}
				}
			}
		}
		else
		{
			getContext().openInformation("Data Manager Editor",
					"Cannot copy current selection");
		}
	}

}
