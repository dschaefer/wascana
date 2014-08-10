package ca.cdtdoug.wascana.arduino.core.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

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

	public static <T> T getService(Class<T> cls) {
		ServiceReference<T> ref = context.getServiceReference(cls);
		return ref == null ? null : context.getService(ref);
	}
}
