package ca.cdtdoug.wascana.arduino.core.internal.remote;

import java.util.Properties;

import org.eclipse.remote.core.IUserAuthenticator;
import org.eclipse.remote.core.api2.AbstractRemoteConnectionManager;
import org.eclipse.remote.core.api2.IRemoteConnection;
import org.eclipse.remote.core.api2.IRemoteConnectionManager;
import org.eclipse.remote.core.api2.IRemoteConnectionWorkingCopy;
import org.eclipse.remote.core.api2.IRemoteServices;
import org.eclipse.remote.core.api2.IRemoteServices.Service;
import org.eclipse.remote.core.exception.RemoteConnectionException;

public class ArduinoConnectionManager extends AbstractRemoteConnectionManager implements IRemoteConnectionManager {

	private final IRemoteServices remoteServices;

	private ArduinoConnectionManager(IRemoteServices remoteServices) {
		this.remoteServices = remoteServices;
	}
	
	public static class Factory implements IRemoteServices.Service.Factory {
		@Override
		public Service getService(IRemoteServices remoteServices) {
			return new ArduinoConnectionManager(remoteServices);
		}
	}

	@Override
	public IRemoteServices getRemoteServices() {
		return remoteServices;
	}

	@Override
	public IUserAuthenticator getUserAuthenticator(IRemoteConnection connection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRemoteConnectionWorkingCopy newConnection(String name) throws RemoteConnectionException {
		return new ArduinoRemoteConnectionWorkingCopy(remoteServices, name);
	}

	@Override
	public IRemoteConnection loadConnection(String name, Properties properties) throws RemoteConnectionException {
		return new ArduinoRemoteConnection(remoteServices, name, properties);
	}

}
