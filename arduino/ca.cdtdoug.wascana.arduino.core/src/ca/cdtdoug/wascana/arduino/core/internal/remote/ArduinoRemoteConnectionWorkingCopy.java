package ca.cdtdoug.wascana.arduino.core.internal.remote;

import org.eclipse.remote.core.api2.AbstractRemoteConnectionWorkingCopy;
import org.eclipse.remote.core.api2.IRemoteConnection;
import org.eclipse.remote.core.api2.IRemoteServices;

import ca.cdtdoug.wascana.arduino.core.remote.Board;
import ca.cdtdoug.wascana.arduino.core.remote.IArduinoRemoteConnection;
import ca.cdtdoug.wascana.arduino.core.remote.IArduinoRemoteConnectionWorkingCopy;

public class ArduinoRemoteConnectionWorkingCopy extends AbstractRemoteConnectionWorkingCopy implements
		IArduinoRemoteConnectionWorkingCopy {

	private String portName;
	private Board board;
	
	public ArduinoRemoteConnectionWorkingCopy(ArduinoRemoteConnection original) {
		super(original);
	}

	public ArduinoRemoteConnectionWorkingCopy(IRemoteServices remoteServices, String name) {
		super(remoteServices, name);
	}
	
	@Override
	public IRemoteConnection getConnection() {
		return this;
	}

	@Override
	public String getPortName() {
		if (portName != null)
			return portName;
		if (getOriginal() != null)
			return getOriginal().getService(IArduinoRemoteConnection.class).getPortName();
		return null;
	}

	@Override
	public Board getBoard() {
		if (board != null)
			return board;
		if (getOriginal() != null)
			return getOriginal().getService(IArduinoRemoteConnection.class).getBoard();
		return null;
	}

	@Override
	public void setPortName(String portName) {
		this.portName = portName;
		setAttribute(PORT_NAME, portName);
	}

	@Override
	public void setBoard(Board board) {
		this.board = board;
		setAttribute(BOARD_ID, board.getId());
	}

	@Override
	protected IRemoteConnection doSave() {
		return new ArduinoRemoteConnection(this);
	}

}
