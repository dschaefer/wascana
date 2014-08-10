package ca.cdtdoug.wascana.arduino.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import ca.cdtdoug.wascana.arduino.core.target.ArduinoTargetRegistry;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		context.registerService(ArduinoTargetRegistry.class, new ArduinoTargetRegistry(), null);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
