package ca.cdtdoug.wascana.arduino.core.launch;

import org.eclipse.cdt.launchbar.core.ILaunchDescriptor;
import org.eclipse.cdt.launchbar.core.ILaunchDescriptorType;
import org.eclipse.cdt.launchbar.core.ProjectLaunchDescriptor;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import ca.cdtdoug.wascana.arduino.core.ArduinoProjectNature;

public class ArduinoLaunchDescriptorType implements ILaunchDescriptorType {

	@Override
	public boolean ownsLaunchObject(Object element) throws CoreException {
		if (element instanceof IProject) {
			return ArduinoProjectNature.hasNature((IProject) element); 
		}

		return false;
	}

	@Override
	public ILaunchDescriptor getDescriptor(Object element) throws CoreException {
		if (element instanceof IProject) {
			return new ProjectLaunchDescriptor(this, (IProject) element);
		}

		return null;
	}

}
