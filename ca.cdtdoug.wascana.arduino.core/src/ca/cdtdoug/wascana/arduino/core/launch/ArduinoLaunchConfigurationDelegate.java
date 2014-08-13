package ca.cdtdoug.wascana.arduino.core.launch;

import java.io.IOException;

import jssc.SerialPortException;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.extension.CConfigurationData;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.cdt.managedbuilder.internal.core.ToolChain;
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
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.osgi.util.tracker.ServiceTracker;

import ca.cdtdoug.wascana.arduino.core.internal.Activator;
import ca.cdtdoug.wascana.arduino.core.target.ArduinoTarget;
import ca.cdtdoug.wascana.arduino.core.target.ArduinoTargetRegistry;

// TODO move this back down to the core. Need to set up declarative services
// so that the console service loads when we launch.
public class ArduinoLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

	private final ServiceTracker<ArduinoTargetRegistry, ArduinoTargetRegistry> targetRegistryServiceTracker;
	private final ArduinoTargetRegistry targetRegistry;

	public ArduinoLaunchConfigurationDelegate() {
		targetRegistryServiceTracker = new ServiceTracker<>(Activator.getContext(), ArduinoTargetRegistry.class, null);
		targetRegistryServiceTracker.open();
		targetRegistry = targetRegistryServiceTracker.getService();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		targetRegistryServiceTracker.close();
	}
	
	@Override
	public boolean buildForLaunch(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		// 1. make sure proper build config is set active
		IProject project = configuration.getMappedResources()[0].getProject();
		ICProjectDescription projDesc = CCorePlugin.getDefault().getProjectDescription(project);
		ICConfigurationDescription configDesc = getBuildConfiguration(projDesc);
		boolean newConfig = false;
		if (configDesc == null) {
			configDesc = createBuildConfiguration(projDesc);
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
	public void launch(ILaunchConfiguration configuration, String mode,
			final ILaunch launch, IProgressMonitor monitor) throws CoreException {
		new Job("Arduino Launch") {
			protected IStatus run(IProgressMonitor monitor) {
				ArduinoTargetRegistry targetRegistry = targetRegistryServiceTracker.getService();
				ArduinoTarget target = targetRegistry.getActiveTarget();
				
				try {
					ArduinoLaunchConsoleService consoleService = getConsoleService();

					target.pauseSerialPort();

					// The build configuration
					
					// The build directory
					
					// The build environment
					
					// The build command
					
					// Run the process and capture the results in the console
					Process process = Runtime.getRuntime().exec("echo hi", null, null);
					consoleService.monitor(process);

					target.resumeSerialPort();
				} catch (CoreException e) {
					return e.getStatus();
				} catch (SerialPortException e) {
					return new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e);
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
	private ICConfigurationDescription getBuildConfiguration(ICProjectDescription projDesc) throws CoreException {
		ArduinoTarget target = targetRegistry.getActiveTarget();
		String boardId = target.getBoard().getId();
		
		for (ICConfigurationDescription configDesc : projDesc.getConfigurations()) {
			IConfiguration config = ManagedBuildManager.getConfigurationForDescription(configDesc);
			IToolChain toolChain = config.getToolChain();
			IOption option = toolChain.getOptionBySuperClassId("ca.cdtdoug.wascana.arduino.core.option.board");
			try {
				String boardType = option.getStringValue();
				if (boardId.equals(boardType))
					return configDesc;
				if (boardType.isEmpty() && boardId.equals("uno")) // the default
					return configDesc;
			} catch (BuildException e) {
				throw new CoreException(new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e));
			}
		}

		return null;
	}
	
	private ICConfigurationDescription createBuildConfiguration(ICProjectDescription projDesc) throws CoreException {
		ArduinoTarget target = targetRegistry.getActiveTarget();
		String boardId = target.getBoard().getId();

		// Need a new one
		ManagedProject managedProject = new ManagedProject(projDesc);
		String configId = ManagedBuildManager.calculateChildId(ArduinoLaunchDescriptorType.avrToolChainId, null);
		IToolChain avrToolChain = ManagedBuildManager.getExtensionToolChain(ArduinoLaunchDescriptorType.avrToolChainId);
		Configuration newConfig = new Configuration(managedProject, (ToolChain) avrToolChain, configId, target.getBoard().getName());
		IToolChain newToolChain = newConfig.getToolChain();
		IOption newOption = newToolChain.getOptionBySuperClassId("ca.cdtdoug.wascana.arduino.core.option.board");
		ManagedBuildManager.setOption(newConfig, newToolChain, newOption, boardId);

		CConfigurationData data = newConfig.getConfigurationData();
		ICConfigurationDescription newConfigDesc = projDesc.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID, data);

		return newConfigDesc;
	}

}
