package ca.cdtdoug.wascana.arduino.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import ca.cdtdoug.wascana.arduino.core.internal.remote.ArduinoBoardManager;
import ca.cdtdoug.wascana.arduino.core.remote.IArduinoBoardManager;

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

	public static void log(Exception e) {
		if (e instanceof CoreException) {
			plugin.getLog().log(((CoreException) e).getStatus());
		} else {
			plugin.getLog().log(new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e));
		}
	}

	public void start(BundleContext bundleContext) throws Exception {
		plugin = this;
		bundleContext.registerService(IArduinoBoardManager.class, new ArduinoBoardManager(), null);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		plugin = null;
	}

	public static <T> T getService(Class<T> service) {
		BundleContext context = plugin.getBundle().getBundleContext();
		ServiceReference<T> ref = context.getServiceReference(service);
		return ref != null ? context.getService(ref) : null;
	}

}
