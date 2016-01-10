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
