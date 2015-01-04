package ca.cdtdoug.wascana.arduino.core.internal.launch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.envvar.IEnvironmentVariable;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.remote.core.api2.IRemoteConnection;
import org.eclipse.remote.core.api2.IRemoteLaunchConfigManagerService;

import ca.cdtdoug.wascana.arduino.core.ArduinoProjectGenerator;
import ca.cdtdoug.wascana.arduino.core.internal.Activator;
import ca.cdtdoug.wascana.arduino.core.remote.IArduinoRemoteConnection;

public class ArduinoLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

	public static ILaunchConfigurationType getLaunchConfigurationType() {
		return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType("ca.cdtdoug.wascana.arduino.core.launchConfigurationType");
	}

	@Override
	public boolean buildForLaunch(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
		// 1. make sure proper build config is set active
		IProject project = configuration.getMappedResources()[0].getProject();
		ICProjectDescription projDesc = CCorePlugin.getDefault().getProjectDescription(project);
		IRemoteConnection remoteConnection = getActiveRemote(configuration);
		if (remoteConnection == null) {
			// TODO default?
			return false;
		}
		ICConfigurationDescription configDesc = getBuildConfiguration(projDesc, remoteConnection);
		boolean newConfig = false;
		if (configDesc == null) {
			IArduinoRemoteConnection arduinoRemote = remoteConnection.getService(IArduinoRemoteConnection.class);
			configDesc = ArduinoProjectGenerator.createBuildConfiguration(projDesc, arduinoRemote.getBoard());
			newConfig = true;
		}
		if (newConfig || !projDesc.getActiveConfiguration().equals(configDesc)) {
			projDesc.setActiveConfiguration(configDesc);
			CCorePlugin.getDefault().setProjectDescription(project, projDesc);
		}

		// 2. Run the build
		return super.buildForLaunch(configuration, mode, monitor);
	}

	@Override
	protected IProject[] getBuildOrder(ILaunchConfiguration configuration, String mode) throws CoreException {
		// 1. Extract project from configuration
		IProject project = configuration.getMappedResources()[0].getProject();
		return new IProject[] { project };
	}

	@Override
	public void launch(final ILaunchConfiguration configuration, String mode,
			final ILaunch launch, IProgressMonitor monitor) throws CoreException {
		new Job("Arduino Launch") {
			protected IStatus run(IProgressMonitor monitor) {
				try {
					ArduinoLaunchConsoleService consoleService = getConsoleService();

					IRemoteConnection connection = getActiveRemote(configuration);
					if (connection == null) {
						return new Status(IStatus.ERROR, Activator.getId(), "No active Arduino remote connection.");
					}
					
					// The project
					IProject project = (IProject) configuration.getMappedResources()[0];

					// The build environment
					ICProjectDescription projDesc = CCorePlugin.getDefault().getProjectDescription(project);
					ICConfigurationDescription configDesc = getBuildConfiguration(projDesc, connection);
					IEnvironmentVariable[] envVars = CCorePlugin.getDefault().getBuildEnvironmentManager().getVariables(configDesc, true);
					List<String> envVarList = new ArrayList<String>(envVars.length + 1);
					for (IEnvironmentVariable var : envVars) {
						envVarList.add(var.getName() + '=' + var.getValue());
					}
					// Add in the serial port based on launch config
					IArduinoRemoteConnection arduinoRemote = connection.getService(IArduinoRemoteConnection.class);
					envVarList.add("SERIAL_PORT=" + arduinoRemote.getPortName());
					String[] envp = envVarList.toArray(new String[envVarList.size()]);

					// The project directory to launch from
					File projectDir = new File(project.getLocationURI());
					
					// The build command
					IConfiguration buildConfig = ManagedBuildManager.getConfigurationForDescription(configDesc);
					String command = buildConfig.getBuilder().getCommand();

					// If opened, temporarily close the connection so we can use it to download the firmware.
					boolean wasOpened = connection.getConnectionStatus().equals(Status.OK_STATUS);
					if (wasOpened) {
						connection.close();
					}

					// Run the process and capture the results in the console
					Process process = Runtime.getRuntime().exec(command + " load", envp, projectDir);
					consoleService.monitor(process);
					try {
						process.waitFor();
					} catch (InterruptedException e) {
					}

					// Reopen the connection
					if (wasOpened) {
						connection.open(monitor);
					}
				} catch (CoreException e) {
					return e.getStatus();
				} catch (IOException e) {
					return new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e);
				} finally {
					DebugPlugin.getDefault().getLaunchManager().removeLaunch(launch);
				}

				return Status.OK_STATUS;
			};
		}.schedule();
	}

	private ArduinoLaunchConsoleService getConsoleService() throws CoreException {
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(Activator.getId(), "consoleService");
		IExtension extension = point.getExtensions()[0]; // should only be one
		return (ArduinoLaunchConsoleService) extension.getConfigurationElements()[0].createExecutableExtension("class");
	}

	/**
	 * Returns the build configuration for the active target and the launch configuration.
	 * 
	 * @param launchConfig
	 * @return
	 */
	private ICConfigurationDescription getBuildConfiguration(ICProjectDescription projDesc, IRemoteConnection remoteConnection) throws CoreException {
		IArduinoRemoteConnection arduinoRemote = remoteConnection.getService(IArduinoRemoteConnection.class);
		String boardId = arduinoRemote.getBoard().getId();

		for (ICConfigurationDescription configDesc : projDesc.getConfigurations()) {
			IConfiguration config = ManagedBuildManager.getConfigurationForDescription(configDesc);
			if (ArduinoProjectGenerator.getBoard(config).getId().equals(boardId))
				return configDesc;
		}

		return null;
	}

	private IRemoteConnection getActiveRemote(ILaunchConfiguration configuration) {
		IRemoteLaunchConfigManagerService remoteLaunchService = Activator.getService(IRemoteLaunchConfigManagerService.class);
		return remoteLaunchService.getActiveRemote(configuration);
	}
}