package ca.cdtdoug.wascana.arduino.core.target;

import java.io.File;

import jssc.SerialPort;

public class ArduinoTarget {

	public ArduinoTarget(String name, String portName, Board board) {

	}

	public String getName() {
		return null;
	}

	public void setTerminalParams(int baudRate, int dataBits, int stopBits, int parity) {
		
	}

	public SerialPort getSerialPort() {
		return null;
	}

	public void download(File hexFile) {
		// If the port is open, close it.
		// Call the downloader, e.g. avrdude
		// If the port was open, reopen it and set the params
	}

}
