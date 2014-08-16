package ca.cdtdoug.wascana.arduino.ui.target;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import ca.cdtdoug.wascana.arduino.core.target.ArduinoTarget;
import ca.cdtdoug.wascana.arduino.core.target.ArduinoTargetRegistry;
import ca.cdtdoug.wascana.arduino.ui.internal.Activator;

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
		try {
			ArduinoTargetRegistry targetRegistry = Activator.getTargetRegistry();
			ArduinoTarget target = new ArduinoTarget(targetRegistry, page.name, page.portName, page.board);
			targetRegistry.addTarget(target);
			return true;
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(e.getStatus());
			return false;
		}
	}

}
