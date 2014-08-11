package ca.cdtdoug.wascana.arduino.core.internal;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import ca.cdtdoug.wascana.arduino.core.target.ArduinoTargetRegistry;

public class Activator extends Plugin {

	private static Plugin plugin;

	public static BundleContext getContext() {
		return plugin.getBundle().getBundleContext();
	}

	public static Plugin getPlugin() {
		return plugin;
	}
	
	public static String getId() {
		return plugin.getBundle().getSymbolicName();
	}

	public void start(BundleContext bundleContext) throws Exception {
		plugin = this;
		getContext().registerService(ArduinoTargetRegistry.class, new ArduinoTargetRegistry(), null);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		plugin = null;
	}

}
