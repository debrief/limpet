package info.limpet.rcp.decorators;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import info.limpet.ICollection;
import info.limpet.ICommand;
import info.limpet.IStore.IStoreItem;
import info.limpet.rcp.Activator;
import info.limpet.rcp.data_provider.data.CollectionWrapper;
import info.limpet.rcp.data_provider.data.CommandWrapper;
import info.limpet.rcp.data_provider.data.GroupWrapper;

public class LimpetDecorator implements ILightweightLabelDecorator
{

	private static final ImageDescriptor TIME;
	
	private static final ImageDescriptor QUANTITY;
	
	private static final ImageDescriptor LEFT_ARROW;

	private static final ImageDescriptor RIGHT_ARROW;

	private static final ImageDescriptor TWO_WAY_ARROW;

	static
	{
		LEFT_ARROW = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
				"icons/left_arrow.png");
		RIGHT_ARROW = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/right_arrow.png");
		TWO_WAY_ARROW = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/two_way_arrow.png");
		TIME = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
				"icons/watch.png");
		QUANTITY = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
				"icons/plus.png");
	}
	
	@Override
	public void addListener(ILabelProviderListener listener)
	{
	}

	@Override
	public void dispose()
	{
	}

	@Override
	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener)
	{
	}

	@Override
	public void decorate(Object element, IDecoration decoration)
	{
		if (element instanceof CollectionWrapper)
		{
			decorateWrapper((CollectionWrapper) element, decoration);
		}
		if (element instanceof CommandWrapper)
		{
			decorateWrapper((CommandWrapper) element, decoration);
		}
		if (element instanceof GroupWrapper)
		{
			decorateWrapper((GroupWrapper) element, decoration);
		}
	}
	
	protected void decorateWrapper(GroupWrapper element, IDecoration decoration)
	{
		// FIXME
	}

	protected void decorateWrapper(CommandWrapper element, IDecoration decoration)
	{
		final ICommand<?> coll = element.getCommand();

		boolean in = coll.getInputs() != null && coll.getInputs().size() > 0;
		boolean out = coll.getOutputs() != null && coll.getOutputs().size() > 0;
		decorateInOut(decoration, in, out);
	}

	protected void decorateWrapper(CollectionWrapper element, IDecoration decoration)
	{
		final IStoreItem item = element.getCollection();
		if (item instanceof ICollection)
		{
			ICollection coll = (ICollection) item;
			boolean out = coll.getPrecedent() != null;
			boolean in = coll.getDependents() != null && coll.getDependents().size() > 0;
			decorateInOut(decoration, in, out);
			
			if (coll.isTemporal())
			{
				decoration.addOverlay(TIME, IDecoration.BOTTOM_RIGHT);
			}
			else if (coll.isQuantity())
			{
				decoration.addOverlay(QUANTITY, IDecoration.BOTTOM_LEFT);
			}
		}
	}

	private void decorateInOut(IDecoration decoration, boolean in, boolean out)
	{
		if (in && out)
		{
			decoration.addOverlay(TWO_WAY_ARROW, IDecoration.TOP_RIGHT);
		}
		else if (out)
		{
			decoration.addOverlay(LEFT_ARROW, IDecoration.TOP_RIGHT);
		}
		else if (in)
		{
			decoration.addOverlay(RIGHT_ARROW, IDecoration.TOP_RIGHT);
		}
	}

}
