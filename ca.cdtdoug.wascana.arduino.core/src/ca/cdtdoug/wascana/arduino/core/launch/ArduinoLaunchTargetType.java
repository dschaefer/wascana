package ca.cdtdoug.wascana.arduino.core.launch;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.launchbar.core.ILaunchBarManager;
import org.eclipse.cdt.launchbar.core.ILaunchTarget;
import org.eclipse.cdt.launchbar.core.ILaunchTargetType;
import org.eclipse.core.runtime.CoreException;
import org.osgi.framework.ServiceReference;

import ca.cdtdoug.wascana.arduino.core.internal.Activator;
import ca.cdtdoug.wascana.arduino.core.target.ArduinoTarget;
import ca.cdtdoug.wascana.arduino.core.target.ArduinoTargetRegistry;

public class ArduinoLaunchTargetType implements ILaunchTargetType, ArduinoTargetRegistry.Listener {

	private ILaunchBarManager manager;
	private ArduinoTargetRegistry targetRegistry;
	private Map<String, ArduinoLaunchTarget> targetMap = new HashMap<>();
	
	@Override
	public void init(ILaunchBarManager manager) {
		this.manager = manager;
		
		ServiceReference<ArduinoTargetRegistry> ref = Activator.getContext().getServiceReference(ArduinoTargetRegistry.class);
		targetRegistry = Activator.getContext().getService(ref);

		targetRegistry.addListener(this);
		ArduinoTarget[] targets = targetRegistry.getTargets();
		for (ArduinoTarget target : targets) {
			ArduinoLaunchTarget launchTarget = new ArduinoLaunchTarget(this, target);
			targetMap.put(launchTarget.getId(), launchTarget);
		}
	}

	ArduinoTargetRegistry getTargetRegistry() {
		return targetRegistry;
	}
	
	@Override
	public String getId() {
		return "ca.cdtdoug.wascana.arduino.core.targetType";
	}

	@Override
	public ILaunchTarget[] getTargets() {
		return targetMap.values().toArray(new ILaunchTarget[targetMap.size()]);
	}

	@Override
	public ILaunchTarget getTarget(String id) {
		return targetMap.get(id);
	}

	@Override
	public void targetAdded(ArduinoTarget target) {
		ArduinoLaunchTarget launchTarget = new ArduinoLaunchTarget(this, target);
		targetMap.put(launchTarget.getId(), launchTarget);
		try {
			manager.updateLaunchTarget(launchTarget);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void targetRemoved(ArduinoTarget target) {
		ArduinoLaunchTarget launchTarget = targetMap.get(ArduinoLaunchTarget.getId(target));
		targetMap.remove(launchTarget);
		try {
			manager.updateLaunchTarget(launchTarget);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
