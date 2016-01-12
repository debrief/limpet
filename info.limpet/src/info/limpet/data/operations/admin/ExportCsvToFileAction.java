/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.data.operations.admin;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IContext.Status;
import info.limpet.IOperation;
import info.limpet.IStore;
import info.limpet.IStoreItem;
import info.limpet.data.commands.AbstractCommand;
import info.limpet.data.operations.admin.CopyCsvToClipboardAction.CopyCsvToClipboardCommand;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provides UI & processing to export a single collection to a CSV file
 * 
 * @author ian
 * 
 */
public class ExportCsvToFileAction implements IOperation<IStoreItem>
{

  /**
   * encapsulate command
   * 
   * @author ian
   * 
   */
  public static class ExportCsvToFileCommand extends
      AbstractCommand<IStoreItem>
  {
    private List<IStoreItem> _selection;

    public ExportCsvToFileCommand(String title, List<IStoreItem> selection,
        IStore store, IContext context)
    {
      super(title, "Export selection to CSV file", store, false, false, null,
          context);
      _selection = selection;
    }

    @Override
    public void execute()
    {
      String csv = CopyCsvToClipboardCommand.getCsvString(_selection);
      if (csv != null && !csv.isEmpty())
      {
        String result = getContext().getCsvFilename();
        if (result == null)
        {
          return;
        }
        File file = new File(result);
        if (file.exists()
            && !getContext().openQuestion("Overwrite '" + result + "'?",
                "Are you sure you want to overwrite '" + result + "'?"))
        {
          return;
        }
        FileOutputStream fop = null;
        try
        {
          fop = new FileOutputStream(file);
          fop.write(csv.getBytes(Charset.forName("UTF-8")));
        }
        catch (IOException e)
        {
          getContext().openError("Error",
              "Cannot write to '" + result + "'. See log for more details");
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

    @Override
    protected void recalculate()
    {
      // don't worry
    }

    @Override
    protected String getOutputName()
    {
      // we don't actually use this
      return null;
    }
  }

  public Collection<ICommand<IStoreItem>> actionsFor(
      List<IStoreItem> selection, IStore destination, IContext context)
  {
    Collection<ICommand<IStoreItem>> res =
        new ArrayList<ICommand<IStoreItem>>();
    if (appliesTo(selection))
    {
      // hmm, see if we have a single collection selected
      ICommand<IStoreItem> newC = null;
      if (selection.size() == 1)
      {
        newC =
            new ExportCsvToFileCommand("Export to CSV file", selection,
                destination, context);
        res.add(newC);
      }
    }

    return res;
  }

  private boolean appliesTo(List<IStoreItem> selection)
  {
    return selection.size() == 1 && selection.get(0) instanceof ICollection;
  }

}
