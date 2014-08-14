package ca.cdtdoug.wascana.arduino.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import ca.cdtdoug.wascana.arduino.core.internal.Activator;

public class ArduinoProjectNature implements IProjectNature {

	private IProject project;
	public static final String ID = Activator.getId() + ".arduinoNature";
	
	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}

	public static boolean hasNature(IProject project) throws CoreException {
		IProjectDescription projDesc = project.getDescription();
		for (String id : projDesc.getNatureIds()) {
			if (id.equals(ID))
				return true;
		}
		return true;
	}

	@Override
	public void configure() throws CoreException {
	}

	@Override
	public void deconfigure() throws CoreException {
	}

}
