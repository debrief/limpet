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
