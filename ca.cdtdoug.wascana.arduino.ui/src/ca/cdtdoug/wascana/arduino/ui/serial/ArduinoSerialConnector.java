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
import org.eclipse.tm.internal.terminal.provisional.api.provider.TerminalConnectorImpl;

import ca.cdtdoug.wascana.arduino.core.target.ArduinoTarget;
import ca.cdtdoug.wascana.arduino.ui.internal.Activator;

@SuppressWarnings("restriction")
public class ArduinoSerialConnector extends TerminalConnectorImpl implements SerialPortEventListener {

	private SerialPort serialPort;
	private String targetName;

	@Override
	public void connect(ITerminalControl control) {
		super.connect(control);
		ArduinoTarget target = Activator.getTargetRegistry().getTarget(targetName);
		if (target == null)
			// Badness
			return;

		try {
			serialPort = target.openSerialPort(
					SerialPort.BAUDRATE_9600,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE,
					this, SerialPort.MASK_RXCHAR);
		} catch (SerialPortException e) {
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e));
			PrintStream out = new PrintStream(fControl.getRemoteToTerminalOutputStream());
			out.println(e.getLocalizedMessage());
		}
	}

	@Override
	protected void doDisconnect() {
		try {
			serialPort.closePort();
		} catch (SerialPortException e) {
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.getId(), e.getLocalizedMessage(), e));
		}
	}

	@Override
	protected void finalize() throws Throwable {
		serialPort.closePort();
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
		targetName = store.get(ArduinoSerialMonitorDelegate.TARGET_NAME);
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

}
