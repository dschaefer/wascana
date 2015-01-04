package ca.cdtdoug.wascana.arduino.core.internal.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.launchbar.core.ProjectLaunchConfigurationProvider;

public class ArduinoLaunchConfigurationProvider extends ProjectLaunchConfigurationProvider {

	@Override
	public ILaunchConfigurationType getLaunchConfigurationType() throws CoreException {
		return ArduinoLaunchConfigurationDelegate.getLaunchConfigurationType();
	}

}
