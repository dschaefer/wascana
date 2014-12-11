package ca.cdtdoug.wascana.arduino.core.launch;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.launchbar.core.ILaunchBarManager;
import org.eclipse.launchbar.core.ILaunchTargetType;
import org.osgi.framework.ServiceReference;

import ca.cdtdoug.wascana.arduino.core.internal.Activator;
import ca.cdtdoug.wascana.arduino.core.target.ArduinoTarget;
import ca.cdtdoug.wascana.arduino.core.target.ArduinoTargetRegistry;

public class ArduinoLaunchTargetType implements ILaunchTargetType, ArduinoTargetRegistry.Listener {

	private ILaunchBarManager manager;
	private ArduinoTargetRegistry targetRegistry;
	private Map<ArduinoTarget, ArduinoLaunchTarget> targetMap = new HashMap<>();

	@Override
	public void init(ILaunchBarManager manager) {
		this.manager = manager;

		ServiceReference<ArduinoTargetRegistry> ref = Activator.getContext().getServiceReference(ArduinoTargetRegistry.class);
		targetRegistry = Activator.getContext().getService(ref);

		ArduinoTarget[] targets = targetRegistry.getTargets();
		for (ArduinoTarget target : targets) {
			targetAdded(target);
		}

		targetRegistry.addListener(this);
	}

	@Override
	public void dispose() {
		targetRegistry.removeListener(this);
	}

	ArduinoTargetRegistry getTargetRegistry() {
		return targetRegistry;
	}

	@Override
	public void targetAdded(ArduinoTarget target) {
		try {
			ArduinoLaunchTarget launchTarget = new ArduinoLaunchTarget(this, target);
			targetMap.put(target, launchTarget);
			manager.launchTargetAdded(launchTarget);
		} catch (CoreException e) {
			Activator.getPlugin().getLog().log(e.getStatus());
		}
	}

	@Override
	public void targetRemoved(ArduinoTarget target) {
		try {
			ArduinoLaunchTarget launchTarget = targetMap.get(target);
			targetMap.remove(target);
			manager.launchTargetRemoved(launchTarget);
		} catch (CoreException e) {
			Activator.getPlugin().getLog().log(e.getStatus());
		}
	}

}
