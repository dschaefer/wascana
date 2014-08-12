package ca.cdtdoug.wascana.arduino.ui.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import ca.cdtdoug.wascana.arduino.ui.launch.ArduinoLaunchConsole;
import ca.cdtdoug.wascana.arduino.ui.launch.ArduinoLaunchConsoleService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ca.cdtdoug.wascana.arduino.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		context.registerService(ArduinoLaunchConsoleService.class, new ArduinoLaunchConsole(), null);
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public static BundleContext getContext() {
		return plugin.getBundle().getBundleContext();
	}

	public static String getId() {
		return plugin.getBundle().getSymbolicName();
	}

	public <T> T getService(Class<T> cls) {
		BundleContext context = plugin.getBundle().getBundleContext();
		ServiceReference<T> ref = context.getServiceReference(cls);
		return ref == null ? null : context.getService(ref);
	}

}
