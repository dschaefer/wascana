package ca.cdtdoug.wascana.arduino.core.launch;

import org.eclipse.cdt.launchbar.core.ILaunchDescriptor;
import org.eclipse.cdt.launchbar.core.ILaunchDescriptorType;
import org.eclipse.core.resources.IProject;

public class ArduinoLaunchDescriptor implements ILaunchDescriptor {

	private final ArduinoLaunchDescriptorType type;
	private final IProject project;

	public ArduinoLaunchDescriptor(ArduinoLaunchDescriptorType type, IProject project) {
		this.project = project;
		this.type = type;
	}

	@Override
	public String getName() {
		return project.getName();
	}

	@Override
	public String getId() {
		return project.getName();
	}

	@Override
	public ILaunchDescriptorType getType() {
		return type;
	}

	public IProject getProject() {
		return project;
	}

}
