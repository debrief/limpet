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
package info.limpet.ui;

import java.util.ArrayList;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

  // The plug-in ID
  public static final String PLUGIN_ID = "info.limpet.ui"; //$NON-NLS-1$

  // The shared instance
  private static Activator plugin;

  private ImageRegistry _imageRegistry;

  /**
   * the adapters we know about
   * 
   */
  private ArrayList<IAdapterFactory> _adapters;

  /**
   * The constructor
   */
  public Activator()
  {
  }

  private static ImageRegistry getRegistry()
  {
    return plugin._imageRegistry;
  }

  public static Image getImageFromRegistry(final ImageDescriptor name)
  {
    Image res = null;

    // do we already have an image
    if (getRegistry() == null)
    {
      plugin._imageRegistry = new ImageRegistry();
    }

    // ok - do we have it already?
    res = getRegistry().get(name.toString());

    if (res == null)
    {
      getRegistry().put(name.toString(), name);
      res = getRegistry().get(name.toString());
    }

    // and return it..
    return res;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
   */
  public void start(BundleContext context) throws Exception
  {
    super.start(context);
    plugin = this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
   */
  public void stop(BundleContext context) throws Exception
  {
    plugin = null;
    super.stop(context);
  }

  /**
   * Returns the shared instance
   * 
   * @return the shared instance
   */
  public static Activator getDefault()
  {
    return plugin;
  }

  /**
   * Returns an image descriptor for the image file at the given plug-in relative path
   * 
   * @param path
   *          the path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor(String path)
  {
    return imageDescriptorFromPlugin(PLUGIN_ID, path);
  }

  public static void logError(int statCode, String message, Exception e)
  {
    IStatus status = new Status(statCode, PLUGIN_ID, message, e);

    final Activator default1 = getDefault();
    if (default1 != null)
    {
      default1.getLog().log(status);
    }
    else
    {
      System.err.println("Logger not assigned. Message:" + message);
      e.printStackTrace();
    }
  }

  public static void log(Exception e)
  {
    IStatus status = new Status(IStatus.WARNING, PLUGIN_ID, e.getMessage(), e);
    getDefault().getLog().log(status);
  }

  /**
   * @param limpetAdapters
   */
  public void addAdapterFactory(IAdapterFactory newFactory)
  {
    if (_adapters == null)
    {
      _adapters = new ArrayList<IAdapterFactory>();
    }
    _adapters.add(newFactory);

  }

  /**
   * @return
   */
  public ArrayList<IAdapterFactory> getAdapters()
  {
    return _adapters;
  }

}
