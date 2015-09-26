package info.limpet.rcp.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import info.limpet.rcp.data_provider.data.DataModel;

/**
 * 
 * Temporary editor input for Limpet Data Provider editor
 * @author snpe
 *
 */
public class DataProviderEditorInput implements IEditorInput
{

	private DataModel model;

	public DataProviderEditorInput(DataModel model)
	{
		this.model = model;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	@Override
	public boolean exists()
	{
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return "Data Provider";
	}

	@Override
	public IPersistableElement getPersistable()
	{
		return null;
	}

	@Override
	public String getToolTipText()
	{
		return "Data Provider";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataProviderEditorInput other = (DataProviderEditorInput) obj;
		if (model == null)
		{
			if (other.model != null)
				return false;
		}
		else if (!model.equals(other.model))
			return false;
		return true;
	}

	public DataModel getModel()
	{
		return model;
	}

}
