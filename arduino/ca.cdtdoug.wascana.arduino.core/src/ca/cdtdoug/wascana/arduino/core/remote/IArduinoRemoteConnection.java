package ca.cdtdoug.wascana.arduino.core.remote;

import org.eclipse.remote.core.api2.IRemoteConnection;


/**
 * Arduino specific extensions to IRemoteConnection.
 * 
 * @author dschaefer
 *
 */
public interface IArduinoRemoteConnection extends IRemoteConnection.Service {

	final String TYPE_ID = "ca.cdtdoug.wascana.arduino.core.remoteServices";
	final String PORT_NAME = "ardiuno.portname";
	final String BOARD_ID = "arduino.board";


	/**
	 * Return the serial port name.
	 * 
	 * @return serial port name
	 */
	String getPortName();

	/**
	 * Get the board type at the end of this connection.
	 * 
	 * @return Board
	 */
	Board getBoard();
	
}
