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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.CopyFilesAndFoldersOperation;
import org.osgi.framework.Bundle;

import info.limpet.ui.Activator;

public class SampleProjectWizardPage extends WizardPage
{

  private String projectName;

  protected SampleProjectWizardPage()
  {
    super("info.limpet.rcp.wizard.sampleProjectWizardPage");
    setTitle("Create Project");
    setDescription("You'll need to create a project to hold your data.  Please provide a name.");
  }

  @Override
  public void createControl(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    composite.setLayout(new GridLayout(2, false));
    new Label(composite, SWT.LEFT).setText("Project name:");
    final Text nameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
    projectName = "Limpet sample data";
    IProject project =
        ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
    if (project != null && project.exists())
    {
      projectName = "";
    }
    nameText.setText(projectName);
    nameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    nameText.addModifyListener(new ModifyListener()
    {
      public void modifyText(ModifyEvent event)
      {
        projectName = nameText.getText();
        validate();
      }
    });
    setControl(composite);
    setPageComplete(false);
    validate();

  }

  private void validate()
  {
    setErrorMessage(null);
    setPageComplete(true);
    if (projectName == null || projectName.isEmpty())
    {
      setErrorMessage("Project name is required");
      setPageComplete(false);
    }
    else
    {
      IProject project =
          ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
      if (project != null && project.exists())
      {
        setErrorMessage("The '" + projectName + "' exists.");
        setPageComplete(false);
      }
    }
  }

  public boolean performFinish()
  {
    try
    {
      IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
      IProject project = root.getProject(projectName);
      project.create(null);
      project.open(null);
      Bundle bundle = Platform.getBundle("info.limpet.sample_data");
      if (bundle == null)
      {
        MessageDialog
            .openWarning(getShell(), "Warning",
                "The sample_data plugin is missing. Please check your installation");
        return true;
      }
      URL entry = bundle.getEntry("/");
      File file = new File(FileLocator.resolve(entry).toURI());
      if (file.isDirectory())
      {
        CopyFilesAndFoldersOperation operation =
            new CopyFilesAndFoldersOperation(getShell());
        List<URI> uris = new ArrayList<URI>();
        File f = new File(file, "data");
        if (f.isDirectory())
        {
          uris.add(f.toURI());
        }
        f = new File(file, "americas_cup");
        if (f.isDirectory())
        {
          uris.add(f.toURI());
        }
        if (uris.size() == 0)
        {
          return true;
        }
        operation.copyFiles(uris.toArray(new URI[0]), project);
      }
    }
    catch (CoreException | URISyntaxException | IOException e)
    {
      MessageDialog.openError(getShell(), "Error",
          "Cannot create project. Reason: " + e.getMessage());
      Activator.log(e);
      return false;
    }
    return true;
  }

}
