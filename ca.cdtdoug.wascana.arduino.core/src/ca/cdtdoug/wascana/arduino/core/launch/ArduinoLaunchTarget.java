package ca.cdtdoug.wascana.arduino.core.launch;

import org.eclipse.cdt.launchbar.core.ILaunchTarget;
import org.eclipse.cdt.launchbar.core.ILaunchTargetType;

import ca.cdtdoug.wascana.arduino.core.target.ArduinoTarget;

public class ArduinoLaunchTarget implements ILaunchTarget {

	private final ArduinoLaunchTargetType type;
	private final ArduinoTarget target;

	public ArduinoLaunchTarget(ArduinoLaunchTargetType type, ArduinoTarget target) {
		this.type = type;
		this.target = target;
	}

	@Override
	public String getId() {
		return getId(target);
	}

	public static String getId(ArduinoTarget target) {
		return target.getName();
	}

	@Override
	public String getName() {
		return target.getName();
	}

	@Override
	public ILaunchTargetType getType() {
		return type;
	}

	@Override
	public void setActive() {
		type.getTargetRegistry().setActiveTarget(target);
	}

}
