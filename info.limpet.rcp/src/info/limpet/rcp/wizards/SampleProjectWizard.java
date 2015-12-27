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
package info.limpet.rcp.wizards;

import org.eclipse.jface.wizard.Wizard;

public class SampleProjectWizard extends Wizard
{

	private SampleProjectWizardPage page;

	public SampleProjectWizard()
	{
		super();
		setForcePreviousAndNextButtons(false);
	}

	@Override
	public String getWindowTitle()
	{
		return "Create Sample Project";
	}

	@Override
	public void addPages()
	{
		page = new SampleProjectWizardPage();
		addPage(page);
	}

	@Override
	public boolean performFinish()
	{
		return page.performFinish();
	}

}
