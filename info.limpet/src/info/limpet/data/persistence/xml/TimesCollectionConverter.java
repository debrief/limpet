/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package info.limpet.data.persistence.xml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

import info.limpet.data.impl.TimesList;

public class TimesCollectionConverter extends CollectionConverter
{

	private static final String XML_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	private static final String TIME_NODE = "time";
	private static final String INFO_LIMPET_PLUGIN_ID = "info.limpet";
	private static SimpleDateFormat _XMLDateFormat;

	static
	{
		_XMLDateFormat = new SimpleDateFormat(XML_TIME_FORMAT);
		_XMLDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public TimesCollectionConverter(Mapper mapper)
	{
		super(mapper);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean canConvert(Class type)
	{
		return TimesList.class.isAssignableFrom(type);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context)
	{
		@SuppressWarnings("unchecked")
		TimesList<Long> times = (TimesList<Long>) source;
		for (Long time : times)
		{
			String value = _XMLDateFormat.format(new Date(time));
			writer.startNode(TIME_NODE);
			context.convertAnother(value);
			writer.endNode();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context)
	{
		List<Long> times = new TimesList<>();
		while (reader.hasMoreChildren())
		{
			reader.moveDown();
			String item = (String) context.convertAnother(times, String.class);
			Long value;
			try
			{
				value = _XMLDateFormat.parse(item).getTime();
			}
			catch (ParseException e)
			{
				try
				{
					value = new Long(item);
				}
				catch (NumberFormatException e1)
				{
					log(e1);
					value = new Date().getTime();
				}
			}
			times.add(value);
			reader.moveUp();
		}
		return times;
	}

	private void log(Throwable t)
	{
		Bundle bundle = Platform.getBundle(INFO_LIMPET_PLUGIN_ID);
		if (bundle != null)
		{
			ILog log = Platform.getLog(bundle);
			if (log != null)
			{
				log.log(new Status(IStatus.WARNING, bundle.getSymbolicName(),
						"XStream time converter", t));
				return;
			}
		}
		t.printStackTrace();
	}

}
