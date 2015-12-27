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
package info.limpet.rcp.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import info.limpet.rcp.wizards.SampleProjectWizard;

public class CreateSampleProject extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		Shell shell = PlatformUI.getWorkbench().getModalDialogShellProvider().getShell();
		WizardDialog dialog = new WizardDialog(shell, new SampleProjectWizard());
		dialog.open();
		return null;
	}

}
