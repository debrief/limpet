package info.limpet.rcp.editors;

import info.limpet.ICollection;
import info.limpet.IQuantityCollection;
import info.limpet.data.impl.ObjectCollection;
import info.limpet.data.impl.samples.StockTypes.NonTemporal;
import info.limpet.data.impl.samples.StockTypes.Temporal;
import info.limpet.data.impl.samples.StockTypes.Temporal.AcousticStrength;
import info.limpet.rcp.Activator;
import info.limpet.rcp.data_provider.data.CollectionWrapper;
import info.limpet.rcp.data_provider.data.CommandWrapper;
import info.limpet.rcp.data_provider.data.DataModel;
import info.limpet.rcp.data_provider.data.GroupWrapper;
import info.limpet.rcp.data_provider.data.NamedList;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.unit.Dimension;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

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
			res = Activator.getImageFromRegistry(Activator
					.getImageDescriptor("icons/folder.png"));
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
				else if (dim.equals(Angle.UNIT.getDimension()))
				{
					res = Activator.getImageFromRegistry(Activator
							.getImageDescriptor("icons/angle.png"));
				}
				else if (dim.equals(Dimension.MASS))
				{
					res = Activator.getImageFromRegistry(Activator
							.getImageDescriptor("icons/weight.png"));
				}
				else if (q instanceof AcousticStrength)
				{
					res = Activator.getImageFromRegistry(Activator
							.getImageDescriptor("icons/volume.png"));
				}
				else if (dim.equals(Dimension.LENGTH.times(Dimension.LENGTH)
						.times(Dimension.LENGTH).divide(Dimension.MASS)))
				{
					res = Activator.getImageFromRegistry(Activator
							.getImageDescriptor("icons/density.png"));
				}
				else if (dim.equals(SI.HERTZ))
				{
					res = Activator.getImageFromRegistry(Activator
							.getImageDescriptor("icons/frequency.png"));
				}
				else if (dim.equals(Dimension.TIME))
				{
					res = Activator.getImageFromRegistry(Activator
							.getImageDescriptor("icons/time.png"));
				}
				else if (dim.equals(Dimensionless.UNIT))
				{
					res = Activator.getImageFromRegistry(Activator
							.getImageDescriptor("icons/numbers.png"));
				}
				else if (dim.equals(Dimension.LENGTH.divide(Dimension.TIME)))
				{
					res = Activator.getImageFromRegistry(Activator
							.getImageDescriptor("icons/speed.png"));
				}
			}
			else if (coll instanceof Temporal.Location
					|| coll instanceof NonTemporal.Location)
			{
				res = Activator.getImageFromRegistry(Activator
						.getImageDescriptor("icons/location.png"));
			}
			else if (coll instanceof ObjectCollection)
			{
				res = Activator.getImageFromRegistry(Activator
						.getImageDescriptor("icons/string.png"));
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
			NamedList nl = (NamedList) obj;
			String name = nl.toString();

			if (name.equals(DataModel.PRECEDENTS))
			{
				res = Activator.getImageFromRegistry(Activator
						.getImageDescriptor("icons/l_arrow.png"));

			}
			else if (name.equals(DataModel.DEPENDENTS))
			{
				res = Activator.getImageFromRegistry(Activator
						.getImageDescriptor("icons/r_arrow.png"));

			}

		}

		return res;
	}
}
