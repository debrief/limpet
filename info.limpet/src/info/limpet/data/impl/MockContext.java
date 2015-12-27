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
package info.limpet.data.impl;

import info.limpet.IContext;

public class MockContext implements IContext
{

	@Override
	public String getInput(String title, String description, String defaultText)
	{
		return defaultText;
	}

	@Override
	public void logError(Status status, String message, Exception e)
	{
		System.err.println("Logging status:" + status + " message:" + message);
		e.printStackTrace();
	}

}
