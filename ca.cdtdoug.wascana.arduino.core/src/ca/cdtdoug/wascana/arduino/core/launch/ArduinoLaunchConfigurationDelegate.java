package ca.cdtdoug.wascana.arduino.core.launch;

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

import ca.cdtdoug.wascana.arduino.core.internal.Activator;
import ca.cdtdoug.wascana.arduino.core.target.ArduinoTarget;
import ca.cdtdoug.wascana.arduino.core.target.ArduinoTargetRegistry;

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
				ServiceReference<ArduinoTargetRegistry> serviceRef = Activator.getContext().getServiceReference(ArduinoTargetRegistry.class);
				if (serviceRef == null) {
					return new Status(IStatus.ERROR, Activator.getId(), "No Target Registry");
				}
				ArduinoTargetRegistry targetRegistry = Activator.getContext().getService(serviceRef);
				ArduinoTarget target = targetRegistry.getActiveTarget();

				try {
					target.pauseSerialPort();

					// TODO call make target to bootload

					target.resumeSerialPort();
				} catch (SerialPortException e) {
					return new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e);
				}

				DebugPlugin.getDefault().getLaunchManager().removeLaunch(launch);
				Activator.getContext().ungetService(serviceRef);
				return Status.OK_STATUS;
			};
		}.schedule();
	}

}
