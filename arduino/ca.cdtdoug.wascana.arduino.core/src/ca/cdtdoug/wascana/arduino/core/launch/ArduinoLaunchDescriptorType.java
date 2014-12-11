package ca.cdtdoug.wascana.arduino.core.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.launchbar.core.ILaunchDescriptor;
import org.eclipse.launchbar.core.ILaunchDescriptorType;
import org.eclipse.launchbar.core.ProjectLaunchDescriptor;

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
