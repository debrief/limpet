package info.limpet.rcp.data_provider;

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
			res = PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_FILE);
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
