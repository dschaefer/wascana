package ca.cdtdoug.wascana.arduino.core.launch;

import org.eclipse.cdt.launchbar.core.ProjectLaunchConfigurationProvider;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;

public class ArduinoLaunchConfigurationProvider extends ProjectLaunchConfigurationProvider {

	@Override
	public ILaunchConfigurationType getLaunchConfigurationType() throws CoreException {
		return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType("ca.cdtdoug.wascana.arduino.core.launchConfigurationType");
	}

}
