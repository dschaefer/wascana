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

public class ArduinoLaunchDescriptorType implements ILaunchDescriptorType {

	private ILaunchBarManager manager;

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
		if (!(element instanceof IProject)) {
			return false;
		}

		IProject project = (IProject) element;
		ICProjectDescription pdesc = CoreModel.getDefault().getProjectDescription(project);
		if (pdesc == null) {
			return false;
		}

		for (ICConfigurationDescription cdesc : pdesc.getConfigurations()) {
			IConfiguration config = ManagedBuildManager.getConfigurationForDescription(cdesc);
			IToolChain toolchain = config.getToolChain();
			IToolChain avrToolchain = ManagedBuildManager.getExtensionToolChain("ca.cdtdoug.wascana.arduino.toolChain.avr");
			if (toolchain.matches(avrToolchain))
				return true;
		}

		return false;
	}

	@Override
	public ILaunchDescriptor getDescriptor(Object element) throws CoreException {
		if (ownsLaunchObject(element))
			return new ArduinoLaunchDescriptor(this, (IProject) element);
		return null;
	}

	@Override
	public ILaunchBarManager getManager() {
		return manager;
	}

}
