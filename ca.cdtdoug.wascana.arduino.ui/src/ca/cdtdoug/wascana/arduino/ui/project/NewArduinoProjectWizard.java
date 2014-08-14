package ca.cdtdoug.wascana.arduino.ui.project;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import ca.cdtdoug.wascana.arduino.core.ArduinoProjectNature;

public class NewArduinoProjectWizard extends BasicNewProjectResourceWizard {

	@Override
	public void addPages() {
		super.addPages();
	}

	@Override
	public boolean performFinish() {
		if (!super.performFinish())
			return false;

		new Job("Creating Aurdino Project") {
			protected IStatus run(IProgressMonitor monitor) {
				try {
					ArduinoProjectNature.setupArduinoProject(getNewProject(), monitor);
					return Status.OK_STATUS;
				} catch (CoreException e) {
					return e.getStatus();
				}
			}
		}.schedule();

		return true;
	}

}
