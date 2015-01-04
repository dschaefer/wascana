package ca.cdtdoug.wascana.arduino.ui.remote;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewArduinoTargetWizard extends Wizard implements INewWizard {

	private NewArduinoTargetWizardPage page;
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		page = new NewArduinoTargetWizardPage();
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		return page.performFinish();
	}

}
