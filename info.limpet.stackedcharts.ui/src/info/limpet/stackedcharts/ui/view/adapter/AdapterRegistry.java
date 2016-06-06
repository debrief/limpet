package info.limpet.stackedcharts.ui.view.adapter;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.ui.editor.Activator;

public class AdapterRegistry implements IStackedAdapter
{

  private static final String ADAPTER_ID = "stacked_adapter";

  @Override
  public Dataset convert(Object data)
  {
    Dataset res = null;
    try
    {
      IConfigurationElement[] config =
          Platform.getExtensionRegistry().getConfigurationElementsFor(
              ADAPTER_ID);
      for (IConfigurationElement e : config)
      {
        Object o = e.createExecutableExtension("class");
        if (o instanceof IStackedAdapter)
        {
          IStackedAdapter sa = (IStackedAdapter) o;
          res = sa.convert(null);
          if (res != null)
          {
            // success, drop out
            break;
          }
        }
      }
    }
    catch (Exception ex)
    {
      Activator.getDefault().getLog().log(
          new Status(Status.ERROR, Activator.PLUGIN_ID,
              "Failed to load stacked charts adapter", ex));
    }
    
    return res;
  }

  @Override
  public boolean canConvert(Object data)
  {
    boolean res = false;
    try
    {
      IConfigurationElement[] config =
          Platform.getExtensionRegistry().getConfigurationElementsFor(
              ADAPTER_ID);
      for (IConfigurationElement e : config)
      {
        Object o = e.createExecutableExtension("class");
        if (o instanceof IStackedAdapter)
        {
          IStackedAdapter sa = (IStackedAdapter) o;
          if (sa.canConvert(data))
          {
            // success, drop out
            res = true;
            break;
          }
        }
      }
    }
    catch (Exception ex)
    {
      Activator.getDefault().getLog().log(
          new Status(Status.ERROR, Activator.PLUGIN_ID,
              "Failed to load stacked charts adapter", ex));
    }
    
    return res;
  }
}