package ca.cdtdoug.wascana.arduino.ui.launch;

import java.io.IOException;

import jssc.SerialPortException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.osgi.framework.ServiceReference;

import ca.cdtdoug.wascana.arduino.core.target.ArduinoTarget;
import ca.cdtdoug.wascana.arduino.core.target.ArduinoTargetRegistry;
import ca.cdtdoug.wascana.arduino.ui.internal.Activator;

public class ArduinoLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

	@Override
	public boolean buildForLaunch(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		// 1. make sure proper build config is set active
		return super.buildForLaunch(configuration, mode, monitor);
	}

	@Override
	protected IProject[] getBuildOrder(ILaunchConfiguration configuration, String mode) throws CoreException {
		// 1. Extract project from configuration
		return super.getBuildOrder(configuration, mode);
	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			final ILaunch launch, IProgressMonitor monitor) throws CoreException {
		new Job("Arduino Launch") {
			protected IStatus run(IProgressMonitor monitor) {
				ServiceReference<ArduinoTargetRegistry> registryRef = Activator.getContext().getServiceReference(ArduinoTargetRegistry.class);
				ArduinoTargetRegistry targetRegistry = Activator.getContext().getService(registryRef);
				ArduinoTarget target = targetRegistry.getActiveTarget();
				
				ServiceReference<ArduinoLaunchConsoleService> consoleRef = Activator.getContext().getServiceReference(ArduinoLaunchConsoleService.class);
				ArduinoLaunchConsoleService consoleService = Activator.getContext().getService(consoleRef);

				try {
					target.pauseSerialPort();

					// The build configuration
					
					// The build directory
					
					// The build environment
					
					// The build command
					
					// Run the process and capture the results in the console
					Process process = Runtime.getRuntime().exec("echo hi", null, null);
					consoleService.monitor(process);

					target.resumeSerialPort();
				} catch (SerialPortException e) {
					return new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e);
				} catch (IOException e) {
					return new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e);
				} finally {
					DebugPlugin.getDefault().getLaunchManager().removeLaunch(launch);
					Activator.getContext().ungetService(registryRef);
					Activator.getContext().ungetService(consoleRef);
				}

				return Status.OK_STATUS;
			};
		}.schedule();
	}

}
