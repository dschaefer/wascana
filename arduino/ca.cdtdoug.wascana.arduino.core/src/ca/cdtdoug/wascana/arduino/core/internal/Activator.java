package ca.cdtdoug.wascana.arduino.core.internal;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import ca.cdtdoug.wascana.arduino.core.target.ArduinoTargetRegistry;

public class Activator extends Plugin {

	private static Plugin plugin;
	private static ArduinoTargetRegistry targetRegistry;

	public static BundleContext getContext() {
		return plugin.getBundle().getBundleContext();
	}

	public static Plugin getPlugin() {
		return plugin;
	}

	public static String getId() {
		return plugin.getBundle().getSymbolicName();
	}

	public static ArduinoTargetRegistry getTargetRegistry() {
		return targetRegistry;
	}

	public void start(BundleContext bundleContext) throws Exception {
		plugin = this;
		targetRegistry = new ArduinoTargetRegistry();
		getContext().registerService(ArduinoTargetRegistry.class, targetRegistry, null);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		plugin = null;
	}

}
