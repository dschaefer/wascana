package ca.cdtdoug.wascana.arduino.core.target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import jssc.SerialPort;
import jssc.SerialPortException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ca.cdtdoug.wascana.arduino.core.internal.Activator;

public class ArduinoTarget {

	private String name;

	private String portName;
	private SerialPort serialPort;
	private int baudRate;
	private int dataBits;
	private int stopBits;
	private int parity;
	boolean paused;

	private Board board;

	private final ArduinoTargetRegistry targetRegistry;

	private static final String nameProp = "name";
	private static final String portProp = "portName";
	private static final String boardProp = "boardId";

	public ArduinoTarget(ArduinoTargetRegistry targetRegistry, String name, String portName, Board board) throws CoreException {
		this.targetRegistry = targetRegistry;
		this.name = name;
		this.portName = portName;
		this.board = board;
		save();
	}

	public ArduinoTarget(ArduinoTargetRegistry targetRegistry, File propertyFile) throws CoreException {
		this.targetRegistry = targetRegistry;

		Properties props = new Properties();
		try {
			props.load(new FileInputStream(propertyFile));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e));
		}
		name = props.getProperty(nameProp);
		portName = props.getProperty(portProp);

		String boardId = props.getProperty(boardProp);
		board = targetRegistry.getBoard(boardId);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) throws CoreException {
		File oldFile = new File(targetRegistry.getTargetsDir(), this.name);
		oldFile.delete();
		save();
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) throws CoreException {
		this.board = board;
		save();
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) throws CoreException {
		this.portName = portName;
		save();
	}

	public SerialPort getSerialPort() {
		if (serialPort == null) {
			serialPort = new SerialPort(portName);
		}
		return null;
	}

	public void setTerminalParams(int baudRate, int dataBits, int stopBits, int parity) {
		this.baudRate = baudRate;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		this.parity = parity;
	}

	public void pauseSerialPort() throws SerialPortException {
		if (serialPort == null)
			return;

		if (!serialPort.isOpened())
			return;

		serialPort.closePort();
		paused = true;
	}

	public void resumeSerialPort() throws SerialPortException {
		if (!paused)
			return;

		serialPort.openPort();
		serialPort.setParams(baudRate, dataBits, stopBits, parity);
	}

	private void save() throws CoreException {
		Properties props = new Properties();
		props.setProperty(nameProp, name);
		props.setProperty(portProp, portName);
		props.setProperty(boardProp, board.getId());

		File targetFile = new File(targetRegistry.getTargetsDir(), name);
		try {
			props.store(new FileOutputStream(targetFile), "target file");
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e));
		}
	}

}
