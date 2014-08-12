package ca.cdtdoug.wascana.arduino.core.launchbar;

import org.eclipse.cdt.launchbar.core.ILaunchBarManager;
import org.eclipse.cdt.launchbar.core.ILaunchConfigurationProvider;
import org.eclipse.cdt.launchbar.core.ILaunchDescriptor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;

public class ArduinoLaunchConfigurationProvider implements ILaunchConfigurationProvider {

	@Override
	public void init(ILaunchBarManager manager) throws CoreException {
		// What to do with the manager
	}

	@Override
	public ILaunchConfigurationType getLaunchConfigurationType(ILaunchDescriptor descriptor) throws CoreException {
		return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType("ca.cdtdoug.wascana.arduino.core.launchConfigurationType");
	}

	@Override
	public ILaunchConfiguration getLaunchConfiguration(ILaunchDescriptor descriptor) throws CoreException {
		String descName = descriptor.getName();
		String name = DebugPlugin.getDefault().getLaunchManager().generateLaunchConfigurationName(descName);
		return getLaunchConfigurationType(descriptor).newInstance(null, name);
	}

	@Override
	public boolean launchConfigurationAdded(ILaunchConfiguration configuration) throws CoreException {
		return false;
	}

	@Override
	public boolean launchConfigurationRemoved(ILaunchConfiguration configuration) throws CoreException {
		return false;
	}

}
