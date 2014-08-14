package ca.cdtdoug.wascana.arduino.core.launch;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.launchbar.core.ILaunchBarManager;
import org.eclipse.cdt.launchbar.core.ILaunchDescriptor;
import org.eclipse.cdt.launchbar.core.ILaunchDescriptorType;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import ca.cdtdoug.wascana.arduino.core.ArduinoProjectNature;
import ca.cdtdoug.wascana.arduino.core.internal.Activator;

public class ArduinoLaunchDescriptorType implements ILaunchDescriptorType {

	private ILaunchBarManager manager;
	
	public static final String avrToolChainId = "ca.cdtdoug.wascana.arduino.toolChain.avr";
	public static final String arduinoLaunchConfigTypeId = "ca.cdtdoug.wascana.arduino.core.launchConfigurationType";

	@Override
	public String getId() {
		return "ca.cdtdoug.wascana.arduino.core.descriptorType";
	}

	@Override
	public void init(ILaunchBarManager manager) {
		this.manager = manager;
	}

	@Override
	public boolean ownsLaunchObject(Object element) throws CoreException {
		if (element instanceof IProject) {
			return ownsProject((IProject) element);
		}

		if (element instanceof ILaunchConfiguration) {
			return ((ILaunchConfiguration) element).getType().getIdentifier().equals(arduinoLaunchConfigTypeId);
		}

		return false;
	}

	private boolean ownsProject(IProject project) {
		try {
			if (ArduinoProjectNature.hasNature(project))
				return true;
		} catch (CoreException e) {
			Activator.getPlugin().getLog().log(e.getStatus());
		}

		ICProjectDescription projDesc = CoreModel.getDefault().getProjectDescription(project);
		if (projDesc == null) // Not a CDT project
			return false;

		for (ICConfigurationDescription configDesc : projDesc.getConfigurations()) {
			IConfiguration config = ManagedBuildManager.getConfigurationForDescription(configDesc);
			IToolChain toolchain = config.getToolChain();
			IToolChain avrToolchain = ManagedBuildManager.getExtensionToolChain(avrToolChainId);
			if (toolchain.matches(avrToolchain))
				return true;
		}

		return false;
	}

	@Override
	public ILaunchDescriptor getDescriptor(Object element) throws CoreException {
		if (element instanceof IProject && ownsProject((IProject) element))
			return new ArduinoLaunchDescriptor(this, (IProject) element);
		return null;
	}

	@Override
	public ILaunchBarManager getManager() {
		return manager;
	}

}
