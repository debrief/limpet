package info.limpet.stackedcharts.ui.view.adapter;

import info.limpet.stackedcharts.ui.editor.Activator;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

public class AdapterRegistry implements IStackedAdapter
{

  private static final String ADAPTER_ID = "info.limpet.stackedcharts.ui.stacked_adapter";

  @Override
  public List<Object> convert(Object data)
  {
    List<Object> res = null;
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
          res = sa.convert(data);
          
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
