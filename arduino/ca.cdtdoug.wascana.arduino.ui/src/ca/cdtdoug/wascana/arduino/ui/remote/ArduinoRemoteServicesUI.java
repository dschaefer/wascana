package ca.cdtdoug.wascana.arduino.ui.remote;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.remote.core.api2.IRemoteServices;
import org.eclipse.remote.core.api2.IRemoteServices.Service;
import org.eclipse.remote.ui.api2.IRemoteServicesUI;
import org.eclipse.swt.graphics.Image;

import ca.cdtdoug.wascana.arduino.ui.internal.Activator;

public class ArduinoRemoteServicesUI extends PlatformObject implements IRemoteServicesUI {

	private final IRemoteServices remoteServices;
	
	public ArduinoRemoteServicesUI(IRemoteServices remoteServices) {
		this.remoteServices = remoteServices;
	}
	
	@Override
	public IRemoteServices getRemoteServices() {
		return remoteServices;
	}

	@Override
	public Image getIcon() {
		return Activator.getDefault().getImage(Activator.IMG_ARDUINO);
	}

	@Override
	public Image getIcon(IStatus status) {
		// TODO overlay with status status
		return getIcon();
	}

	@Override
	public IWizard getNewWizard() {
		return new NewArduinoTargetWizard();
	}

	public static class Factory implements IRemoteServices.Service.Factory {
		@Override
		public Service getService(IRemoteServices remoteServices) {
			return new ArduinoRemoteServicesUI(remoteServices);
		}
	}

}
