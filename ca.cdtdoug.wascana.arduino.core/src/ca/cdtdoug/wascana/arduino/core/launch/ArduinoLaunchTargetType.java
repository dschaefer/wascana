package ca.cdtdoug.wascana.arduino.core.launch;

import org.eclipse.cdt.launchbar.core.ILaunchBarManager;
import org.eclipse.cdt.launchbar.core.ILaunchTarget;
import org.eclipse.cdt.launchbar.core.ILaunchTargetType;

public class ArduinoLaunchTargetType implements ILaunchTargetType {

	@Override
	public void init(ILaunchBarManager manager) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getId() {
		return "ca.cdtdoug.wascana.arduino.core.targetType";
	}

	@Override
	public ILaunchTarget[] getTargets() {
		return new ILaunchTarget[0];
	}

	@Override
	public ILaunchTarget getTarget(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
