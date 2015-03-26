package ca.cdtdoug.wascana.arduino.core.internal.remote;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.cdt.utils.serial.SerialPort;
import org.eclipse.remote.core.IRemoteCommandShellService;
import org.eclipse.remote.core.IRemoteConnection;
import org.eclipse.remote.core.IRemoteConnectionPropertyService;
import org.eclipse.remote.core.IRemoteProcess;
import org.eclipse.remote.core.IRemoteProcessBuilder;

import ca.cdtdoug.wascana.arduino.core.internal.Activator;
import ca.cdtdoug.wascana.arduino.core.remote.Board;
import ca.cdtdoug.wascana.arduino.core.remote.IArduinoBoardManager;
import ca.cdtdoug.wascana.arduino.core.remote.IArduinoRemoteConnection;

public class ArduinoRemoteConnection
implements IRemoteConnectionPropertyService, IRemoteCommandShellService, IRemoteProcess, IArduinoRemoteConnection {

	private final IArduinoBoardManager boardManager = Activator.getService(IArduinoBoardManager.class);
	private final IRemoteConnection remoteConnection;
	private SerialPort serialPort;
	private Object mutex = new Object();

	public ArduinoRemoteConnection(IRemoteConnection remoteConnection) {
		this.remoteConnection = remoteConnection;
	}

	public static class Factory implements IRemoteConnection.Service.Factory {
		@SuppressWarnings("unchecked")
		@Override
		public <T extends IRemoteConnection.Service> T getService(IRemoteConnection remoteConnection, Class<T> service) {
			if (IArduinoRemoteConnection.class.equals(service)) {
				return (T) new ArduinoRemoteConnection(remoteConnection);
			} else if (IRemoteConnectionPropertyService.class.equals(service)
					|| IRemoteCommandShellService.class.equals(service)) {
				return (T) remoteConnection.getService(IArduinoRemoteConnection.class);
			}
			return null;
		}
	}

	@Override
	public IRemoteConnection getRemoteConnection() {
		return remoteConnection;
	}

	@Override
	public String getProperty(String key) {
		if (IRemoteConnection.OS_NAME_PROPERTY.equals(key)) {
			return "arduino";
		} else if (IRemoteConnection.OS_ARCH_PROPERTY.equals(key)) {
			return "avr"; // TODO handle arm
		} else {
			return null;
		}
	}

	@Override
	public Board getBoard() {
		String boardId = remoteConnection.getAttribute(BOARD_ID);
		return boardManager.getBoard(boardId);
	}

	@Override
	public String getPortName() {
		return remoteConnection.getAttribute(PORT_NAME);
	}


	@Override
	public IRemoteProcess getCommandShell(int flags) throws IOException {
		if (serialPort != null) {
			// can only have one open at a time
			return null;
		}

		serialPort = new SerialPort(getPortName());
		resume();
		return this;
	}

	@Override
	public InputStream getErrorStream() {
		return new InputStream() {
			@Override
			public int read() throws IOException {
				synchronized (mutex) {
					while (serialPort != null) {
						try {
							mutex.wait();
						} catch (InterruptedException e) {
							Activator.log(e);
						}
					}
				}
				// we're done
				return -1;
			}

		};
	}

	@Override
	public InputStream getInputStream() {
		return new InputStream() {
			InputStream serialIn = serialPort.getInputStream();

			@Override
			public int read() throws IOException {
				return serialIn.read();
			}

			@Override
			public void close() throws IOException {
				serialIn.close();
				serialPort = null;
			}
		};
	}

	@Override
	public OutputStream getOutputStream() {
		return new OutputStream() {
			OutputStream serialOut = serialPort.getOutputStream();
			
			@Override
			public void write(int b) throws IOException {
				serialOut.write(b);
			}
			
			@Override
			public void close() throws IOException {
				serialOut.close();
				serialPort = null;
			}
		};
	}

	@Override
	public void destroy() {
		pause();
		synchronized (mutex) {
			try {
				serialPort.close();
			} catch (IOException e) {
				Activator.log(e);
			}
			serialPort = null;
			mutex.notifyAll();
		}
	}

	@Override
	public int exitValue() {
		return 0;
	}

	@Override
	public int waitFor() throws InterruptedException {
		synchronized (mutex) {
			while (serialPort != null) {
				mutex.wait();
			}
		}
		return 0;
	}

	@Override
	public boolean isCompleted() {
		return serialPort == null;
	}

	@Override
	public void pause() {
		if (serialPort != null) {
			try {
				if (serialPort.isOpen())
					serialPort.close();
			} catch (IOException e) {
				Activator.log(e);
			}
		}
	}

	@Override
	public void resume() {
		if (serialPort != null) {
			try {
				serialPort.open();
			} catch (IOException e) {
				Activator.log(e);
			}
		}
	}

	@Override
	public <T extends Service> T getService(Class<T> service) {
		return null;
	}

	@Override
	public <T extends Service> boolean hasService(Class<T> service) {
		return false;
	}

	@Override
	public IRemoteProcessBuilder getProcessBuilder() {
		return null;
	}

}
