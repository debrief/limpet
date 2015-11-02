package info.limpet.rcp.editors;

import javax.measure.unit.Dimension;

import info.limpet.ICollection;
import info.limpet.IObjectCollection;
import info.limpet.IQuantityCollection;
import info.limpet.data.impl.samples.StockTypes.NonTemporal;
import info.limpet.data.impl.samples.StockTypes.Temporal;
import info.limpet.rcp.Activator;
import info.limpet.rcp.data_provider.data.CollectionWrapper;
import info.limpet.rcp.data_provider.data.CommandWrapper;
import info.limpet.rcp.data_provider.data.GroupWrapper;
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

		if (obj instanceof GroupWrapper)
		{
			// is it just one, or multiple?
			res = PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_FOLDER);
		}
		if (obj instanceof CollectionWrapper)
		{
			// is it just one, or multiple?
			CollectionWrapper cw = (CollectionWrapper) obj;
			ICollection coll = cw.getCollection();
			if (coll.isQuantity())
			{
				IQuantityCollection<?> q = (IQuantityCollection<?>) coll;
				Dimension dim = q.getUnits().getDimension();
				if (dim.equals(Dimension.LENGTH))
				{
					res = Activator.getImageFromRegistry(Activator
							.getImageDescriptor("icons/measure.png"));
				}
				else if (dim.equals(Dimension.TIME))
				{
					res = Activator.getImageFromRegistry(Activator
							.getImageDescriptor("icons/time.png"));
				}
				else if (dim.equals(Dimension.LENGTH.divide(Dimension.TIME)))
				{
					res = Activator.getImageFromRegistry(Activator
							.getImageDescriptor("icons/speed.png"));
				}				
			}
			else if(coll instanceof Temporal.Location || coll instanceof NonTemporal.Location)
			{
				res = Activator.getImageFromRegistry(Activator
						.getImageDescriptor("icons/location.png"));
			}
			if (res == null)
			{
				res = PlatformUI.getWorkbench().getSharedImages()
						.getImage(ISharedImages.IMG_OBJ_FILE);
			}
		}
		else if (obj instanceof CommandWrapper)
		{
			res = Activator.getImageFromRegistry(Activator
					.getImageDescriptor("icons/interpolate.png"));
		}
		else if (obj instanceof NamedList)
		{
			res = PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_FOLDER);
		}

		return res;
	}
}
