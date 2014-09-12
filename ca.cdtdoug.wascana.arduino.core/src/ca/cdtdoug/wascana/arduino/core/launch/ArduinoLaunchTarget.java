package ca.cdtdoug.wascana.arduino.core.launch;

import org.eclipse.cdt.launchbar.core.ILaunchTarget;
import org.eclipse.cdt.launchbar.core.ILaunchTargetType;
import org.eclipse.core.runtime.PlatformObject;

import ca.cdtdoug.wascana.arduino.core.target.ArduinoTarget;

public class ArduinoLaunchTarget extends PlatformObject implements ILaunchTarget {

	private final ArduinoLaunchTargetType type;
	private final ArduinoTarget target;

	public ArduinoLaunchTarget(ArduinoLaunchTargetType type, ArduinoTarget target) {
		this.type = type;
		this.target = target;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		if (ArduinoTarget.class.equals(adapter)) {
			return target;
		}
		return super.getAdapter(adapter);
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
	public void setActive(boolean active) {
		if (active) {
			type.getTargetRegistry().setActiveTarget(target);
		}
	}

}
