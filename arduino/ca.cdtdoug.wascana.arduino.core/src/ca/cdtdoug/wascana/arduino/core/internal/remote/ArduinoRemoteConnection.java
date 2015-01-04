package ca.cdtdoug.wascana.arduino.core.internal.remote;

import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.remote.core.api2.AbstractRemoteConnection;
import org.eclipse.remote.core.api2.IRemoteConnection;
import org.eclipse.remote.core.api2.IRemoteConnectionWorkingCopy;
import org.eclipse.remote.core.api2.IRemoteServices;
import org.eclipse.remote.core.exception.RemoteConnectionException;

import ca.cdtdoug.wascana.arduino.core.internal.Activator;
import ca.cdtdoug.wascana.arduino.core.remote.Board;
import ca.cdtdoug.wascana.arduino.core.remote.IArduinoBoardManager;
import ca.cdtdoug.wascana.arduino.core.remote.IArduinoRemoteConnection;

public class ArduinoRemoteConnection extends AbstractRemoteConnection implements IArduinoRemoteConnection {

	private String portName;
	private Board board;
	private boolean isOpen;

	public ArduinoRemoteConnection(IRemoteServices remoteServices, String name, Properties properties) {
		super(remoteServices, name, properties);
		init();
	}

	public ArduinoRemoteConnection(ArduinoRemoteConnectionWorkingCopy workingCopy) {
		super(workingCopy);
		init();
	}

	private void init() {
		portName = getAttributes().get(PORT_NAME);
		
		IArduinoBoardManager boardManager = Activator.getService(IArduinoBoardManager.class);
		String boardId = getAttributes().get(BOARD_ID);
		if (boardId != null) {
			board = boardManager.getBoard(boardId);
		}
	}

	@Override
	public IRemoteConnection getConnection() {
		return this;
	}

	@Override
	public boolean isOpen() {
		return isOpen;
	}
	
	@Override
	public IStatus getConnectionStatus() {
		if (isOpen) {
			return Status.OK_STATUS;
		} else {
			return Status.CANCEL_STATUS;
		}
	}

	@Override
	public void open(IProgressMonitor monitor) throws RemoteConnectionException {
		isOpen = true;
		// TODO do I want to open the serial port at this point? or wait for clients.
	}

	@Override
	public void close() {
		isOpen = false;
	}

	@Override
	public String getProperty(String key) {
		if (OS_NAME_PROPERTY.equals("key"))
			return "arduino";
		return null;
	}

	@Override
	public IRemoteConnectionWorkingCopy getWorkingCopy() {
		return new ArduinoRemoteConnectionWorkingCopy(this);
	}
	
	@Override
	public Board getBoard() {
		return board;
	}

	@Override
	public String getPortName() {
		return portName;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Service> T getService(Class<T> service) {
		if (IArduinoRemoteConnection.class.equals(service))
			return (T)this;
		else
			return super.getService(service);
	}

}
