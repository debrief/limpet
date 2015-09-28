package info.limpet.rcp.data_provider;

import info.limpet.rcp.Activator;
import info.limpet.rcp.data_provider.data.CollectionWrapper;
import info.limpet.rcp.data_provider.data.CommandWrapper;
import info.limpet.rcp.data_provider.data.NamedList;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class LimpetLabelProvider extends LabelProvider
{

	@Override
	public Image getImage(Object obj)
	{
		Image res = null;

		if (obj instanceof CollectionWrapper)
		{
			// is it just one, or multiple?
			CollectionWrapper cw = (CollectionWrapper) obj;
			if(cw.getCollection().size()>1)
			{
				res = PlatformUI.getWorkbench().getSharedImages()
						.getImage(ISharedImages.IMG_OBJ_FILE);
			}
			else
			{
				res = Activator.getImageFromRegistry(Activator.getImageDescriptor("icons/number_icon.png"));
			}
		}
		else if (obj instanceof CommandWrapper)
		{
			res = PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_DEF_VIEW);
		}
		else if (obj instanceof NamedList)
		{
			res = PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_FOLDER);
		}

		return res;
	}
}
