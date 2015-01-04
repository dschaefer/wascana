package ca.cdtdoug.wascana.arduino.ui.internal;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ca.cdtdoug.wascana.arduino.ui"; //$NON-NLS-1$

	public static final String IMG_ARDUINO = "arduino";
	
	// The shared instance
	private static Activator plugin;
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		ImageRegistry imageRegistry = getImageRegistry();
		imageRegistry.put(IMG_ARDUINO, imageDescriptorFromPlugin(PLUGIN_ID, "icons/logo.png"));
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

	public Image getImage(String id) {
		return getImageRegistry().get(id);
	}

	public static <T> T getService(Class<T> service) {
		BundleContext context = plugin.getBundle().getBundleContext();
		ServiceReference<T> ref = context.getServiceReference(service);
		return ref != null ? context.getService(ref) : null;
	}

}
