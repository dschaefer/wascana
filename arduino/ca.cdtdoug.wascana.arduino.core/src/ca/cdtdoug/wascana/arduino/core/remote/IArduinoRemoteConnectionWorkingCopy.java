package ca.cdtdoug.wascana.arduino.core.remote;

import org.eclipse.remote.core.api2.IRemoteConnectionWorkingCopy;

public interface IArduinoRemoteConnectionWorkingCopy
extends IRemoteConnectionWorkingCopy.WorkingCopyService, IArduinoRemoteConnection {

	void setPortName(String portName);
	
	void setBoard(Board board);

}
