package ca.cdtdoug.wascana.arduino.ui.target;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import ca.cdtdoug.wascana.arduino.core.target.ArduinoTarget;
import ca.cdtdoug.wascana.arduino.core.target.ArduinoTargetRegistry;
import ca.cdtdoug.wascana.arduino.ui.Activator;

public class NewArduinoTargetWizard extends Wizard implements INewWizard {

	private ArduinoTargetRegistry targetRegistry = Activator.getDefault().getService(ArduinoTargetRegistry.class);
	private NewArduinoTargetWizardPage page;
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		page = new NewArduinoTargetWizardPage(targetRegistry);
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		ArduinoTarget target;
		try {
			target = new ArduinoTarget(targetRegistry, page.name, page.portName, page.board);
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(e.getStatus());
			return false;
		}
		targetRegistry.addTarget(target);
		return true;
	}

}
