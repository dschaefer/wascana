package ca.cdtdoug.wascana.arduino.core;

import org.eclipse.cdt.core.CCProjectNature;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.extension.CConfigurationData;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.cdt.managedbuilder.internal.core.ToolChain;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.util.tracker.ServiceTracker;

import ca.cdtdoug.wascana.arduino.core.internal.Activator;
import ca.cdtdoug.wascana.arduino.core.launch.ArduinoLaunchDescriptorType;
import ca.cdtdoug.wascana.arduino.core.target.ArduinoTarget;
import ca.cdtdoug.wascana.arduino.core.target.ArduinoTargetRegistry;

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

	public static void setupArduinoProject(IProject project, IProgressMonitor monitor) throws CoreException {
		// Add the natures
		IProjectDescription projDesc = project.getDescription();
		CCorePlugin.getDefault().createCDTProject(projDesc, project, monitor);
		
		String[] oldIds = projDesc.getNatureIds();
		String[] newIds = new String[oldIds.length + 3];
		System.arraycopy(oldIds, 0, newIds, 0, oldIds.length);
		newIds[newIds.length - 1] = ArduinoProjectNature.ID;
		newIds[newIds.length - 2] = CCProjectNature.CC_NATURE_ID;
		newIds[newIds.length - 3] = CProjectNature.C_NATURE_ID;
		projDesc.setNatureIds(newIds);
		project.setDescription(projDesc, monitor);

		ICProjectDescription cprojDesc = CCorePlugin.getDefault().createProjectDescription(project, false);
		ManagedBuildInfo info = ManagedBuildManager.createBuildInfo(project);
		ManagedProject mProj = new ManagedProject(cprojDesc);
		info.setManagedProject(mProj);
		
		ServiceTracker<ArduinoTargetRegistry, ArduinoTargetRegistry> targetRegistryServiceTracker = new ServiceTracker<>(Activator.getContext(), ArduinoTargetRegistry.class, null);
		targetRegistryServiceTracker.open();
		ArduinoTargetRegistry targetRegistry = targetRegistryServiceTracker.getService();
		ArduinoTarget target = targetRegistry.getActiveTarget();
		if (target == null) {
			ArduinoTarget[] targets = targetRegistry.getTargets();
			if (targets.length > 0)
				target = targets[0];
		}
		
		if (target != null) {
			target.createBuildConfigurationForTarget(cprojDesc);
		} else {
			String configId = ManagedBuildManager.calculateChildId(ArduinoLaunchDescriptorType.avrToolChainId, null);
			IToolChain avrToolChain = ManagedBuildManager.getExtensionToolChain(ArduinoLaunchDescriptorType.avrToolChainId);
			Configuration newConfig = new Configuration(mProj, (ToolChain) avrToolChain, configId, "uno");
			IToolChain newToolChain = newConfig.getToolChain();
			IOption newOption = newToolChain.getOptionBySuperClassId("ca.cdtdoug.wascana.arduino.core.option.board");
			ManagedBuildManager.setOption(newConfig, newToolChain, newOption, "uno");

			CConfigurationData data = newConfig.getConfigurationData();
			cprojDesc.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID, data);
		}

		CCorePlugin.getDefault().setProjectDescription(project, cprojDesc, true, monitor);

		// 2. Generate the source folder and source file

		// 3. Generate the Makefile

	}

	@Override
	public void configure() throws CoreException {
	}

	@Override
	public void deconfigure() throws CoreException {
	}

}
