package info.limpet.actions;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.opengis.geometry.Geometry;

import info.limpet.IContext;
import info.limpet.IContext.Status;
import info.limpet.IStore;
import info.limpet.data.impl.samples.StockTypes.NonTemporal;
import info.limpet.data.operations.spatial.GeoSupport;
import info.limpet.data.store.IGroupWrapper;

public class CreateLocationAction extends AbstractLimpetAction
{

	public CreateLocationAction(IContext context)
	{
		super(context);
		setText("Create single location");
		setImageDescriptor(context.getImageDescriptor("icons/variable.png"));
	}

	@Override
	public void run()
	{
		// get the name
		String seriesName = "new single location";

		InputDialog dlgName = new InputDialog(Display.getCurrent().getActiveShell(),
				"New fixed location", "Enter name for location", "", null);
		if (dlgName.open() == Window.OK)
		{
			// User clicked OK; update the label with the input
			seriesName = dlgName.getValue();
		}
		else
		{
			return;
		}

		InputDialog dlgValue = new InputDialog(
				Display.getCurrent().getActiveShell(), "New location",
				"Enter initial value for latitude", "", null);
		if (dlgValue.open() == Window.OK)
		{
			// User clicked OK; update the label with the input
			String strLat = dlgValue.getValue();

			// ok, now the second one
			dlgValue = new InputDialog(Display.getCurrent().getActiveShell(),
					"New location", "Enter initial value for longitude", "", null);
			if (dlgValue.open() == Window.OK)
			{
				// User clicked OK; update the label with the input
				String strLong = dlgValue.getValue();

				// ok, now the second one

				try
				{

					NonTemporal.Location newData = new NonTemporal.Location(seriesName);

					// add the new value
					double dblLat = Double.parseDouble(strLat);
					double dblLong = Double.parseDouble(strLong);

					Geometry newLoc = GeoSupport.getBuilder().createPoint(dblLong,
							dblLat);
					newData.add(newLoc);

					// put the new collection in to the selected folder, or into root
					ISelection selection = getSelection();
					IStructuredSelection stru = (IStructuredSelection) selection;
					Object first = stru.getFirstElement();
					if (first instanceof IGroupWrapper)
					{
						IGroupWrapper gW = (IGroupWrapper) first;
						gW.getGroup().add(newData);
					}
					else
					{
						// just store it at the top level
						IStore store = getStore();
						if (store != null)
						{
							store.add(newData);
						}
					}

				}
				catch (NumberFormatException e)
				{
					getContext().logError(Status.WARNING, "Failed to parse initial value",
							e);
					return;
				}
			}
		}
	}

}
