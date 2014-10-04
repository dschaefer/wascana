package ca.cdtdoug.wascana.arduino.ui.internal;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import ca.cdtdoug.wascana.arduino.core.target.ArduinoTargetRegistry;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ca.cdtdoug.wascana.arduino.ui"; //$NON-NLS-1$

	public static final String IMG_ARDUINO = "arduino";
	
	// The shared instance
	private static Activator plugin;
	
	private static ServiceTracker<ArduinoTargetRegistry, ArduinoTargetRegistry> targetRegistryServiceTracker;
	private static ArduinoTargetRegistry targetRegistry;

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		targetRegistryServiceTracker = new ServiceTracker<>(context, ArduinoTargetRegistry.class, null);
		targetRegistryServiceTracker.open();
		targetRegistry = targetRegistryServiceTracker.getService();

		ImageRegistry imageRegistry = getImageRegistry();
		imageRegistry.put(IMG_ARDUINO, imageDescriptorFromPlugin(PLUGIN_ID, "icons/logo.png"));
	}

	public void stop(BundleContext context) throws Exception {
		targetRegistryServiceTracker.close();
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

	public static ArduinoTargetRegistry getTargetRegistry() {
		return targetRegistry;
	}

	public Image getImage(String id) {
		return getImageRegistry().get(id);
	}

}
