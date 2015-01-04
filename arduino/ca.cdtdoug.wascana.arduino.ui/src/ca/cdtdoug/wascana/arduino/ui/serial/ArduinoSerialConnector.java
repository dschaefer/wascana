package ca.cdtdoug.wascana.arduino.ui.serial;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.tm.internal.terminal.provisional.api.ISettingsStore;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalControl;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalState;
import org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl;

import ca.cdtdoug.wascana.arduino.ui.internal.Activator;

@SuppressWarnings("restriction")
public class ArduinoSerialConnector extends TerminalConnectorImpl implements SerialPortEventListener {

	private SerialPort serialPort;
	private int baudrate = SerialPort.BAUDRATE_9600;
	private int databits = SerialPort.DATABITS_8;
	private int stopbits = SerialPort.STOPBITS_1;
	private int parity = SerialPort.PARITY_NONE;
	
	@Override
	public void connect(ITerminalControl control) {
		super.connect(control);
		resume();
		control.setState(TerminalState.CONNECTED);
	}

	@Override
	protected void doDisconnect() {
		pause();
	}

	@Override
	public OutputStream getTerminalToRemoteStream() {
		return new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				try {
					serialPort.writeByte((byte)b);
				} catch (SerialPortException e) {
					throw new IOException(e);
				}
			}

			@Override
			public void write(byte[] b) throws IOException {
				try {
					serialPort.writeBytes(b);
				} catch (SerialPortException e) {
					throw new IOException(e);
				}
			}

		};
	}

	@Override
	public String getSettingsSummary() {
		// nothing
		return null;
	}

	@Override
	public void load(ISettingsStore store) {
//		String targetName = store.get(ArduinoSerialMonitorDelegate.TARGET_NAME);
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		if (event.isRXCHAR()) {
			try {
				byte[] bytes = serialPort.readBytes(event.getEventValue());
				OutputStream out = fControl.getRemoteToTerminalOutputStream();
				out.write(bytes);
			} catch (SerialPortException e) {
				Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e));
			} catch (IOException e) {
				Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e));
			}
		}
	}

	public void pause() {
		if (serialPort == null)
			return;
		
		try {
			if (serialPort.isOpened())
				serialPort.closePort();
			PrintStream out = new PrintStream(fControl.getRemoteToTerminalOutputStream());
			out.print("\r\n--- Stopped ---\r\n");
		} catch (SerialPortException e) {
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e));
			PrintStream out = new PrintStream(fControl.getRemoteToTerminalOutputStream());
			out.println(e.getLocalizedMessage());
		}
	}

	public void resume() {
		try {
			if (serialPort == null)
				serialPort = new SerialPort("");
			else {
				PrintStream out = new PrintStream(fControl.getRemoteToTerminalOutputStream());
				out.print("+++ Restart +++\r\n");
			}
			serialPort.openPort();
			serialPort.setParams(baudrate, databits, stopbits, parity);
			serialPort.addEventListener(this, SerialPort.MASK_RXCHAR);
		} catch (SerialPortException e) {
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e));
			PrintStream out = new PrintStream(fControl.getRemoteToTerminalOutputStream());
			out.println(e.getLocalizedMessage());
		}
	}

}
