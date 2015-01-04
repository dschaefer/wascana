package ca.cdtdoug.wascana.arduino.ui.remote;

import java.util.Collection;

import jssc.SerialPortList;

import org.eclipse.remote.core.api2.IRemoteConnection;
import org.eclipse.remote.core.api2.IRemoteConnectionWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import ca.cdtdoug.wascana.arduino.core.remote.Board;
import ca.cdtdoug.wascana.arduino.core.remote.IArduinoBoardManager;
import ca.cdtdoug.wascana.arduino.core.remote.IArduinoRemoteConnection;
import ca.cdtdoug.wascana.arduino.core.remote.IArduinoRemoteConnectionWorkingCopy;
import ca.cdtdoug.wascana.arduino.ui.internal.Activator;

public class ArduinoTargetPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

	private Combo portSelector;
	private Combo boardSelector;
	
	private Board[] boards;

	@Override
	protected Control createContents(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));

		IRemoteConnection remoteConnection = (IRemoteConnection) getElement().getAdapter(IRemoteConnection.class);
		IArduinoRemoteConnection arduinoRemote = remoteConnection.getService(IArduinoRemoteConnection.class);

		Label portLabel = new Label(comp, SWT.NONE);
		portLabel.setText("Serial Port:");

		portSelector = new Combo(comp, SWT.READ_ONLY);
		portSelector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		String currentPort = arduinoRemote.getPortName();
		int i = 0, portSel = -1;
		for (String port : SerialPortList.getPortNames()) {
			portSelector.add(port);
			if (port.equals(currentPort)) {
				portSel = i;
			} else {
				portSel = portSel < 0 ? 0 : portSel;
			}
			i++;
		}
		if (portSel >= 0) {
			portSelector.select(portSel);
		} else {
			setMessage("No serial ports", ERROR);
			setValid(false);
		}

		Label boardLabel = new Label(comp, SWT.NONE);
		boardLabel.setText("Board type:");

		boardSelector = new Combo(comp, SWT.READ_ONLY);
		boardSelector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Board currentBoard = arduinoRemote.getBoard();
		IArduinoBoardManager boardManager = Activator.getService(IArduinoBoardManager.class);
		Collection<Board> boardList = boardManager.getBoards();
		boards = new Board[boardList.size()];
		i = 0;
		int boardSel = 0;
		for (Board board : boardList) {
			boards[i] = board;
			boardSelector.add(board.getName());
			if (board.equals(currentBoard)) {
				boardSel = i;
			}
			i++;
		}
		boardSelector.select(boardSel);

		return comp;
	}

	@Override
	public boolean performOk() {
		IRemoteConnection remoteConnection = (IRemoteConnection) getElement().getAdapter(IRemoteConnection.class);
		IRemoteConnectionWorkingCopy workingCopy = remoteConnection.getWorkingCopy();
		IArduinoRemoteConnectionWorkingCopy arduinoWorkingCopy = workingCopy.getWorkingCopyService(IArduinoRemoteConnectionWorkingCopy.class);

		String portName = portSelector.getItem(portSelector.getSelectionIndex());
		arduinoWorkingCopy.setPortName(portName);

		Board board = boards[boardSelector.getSelectionIndex()];
		arduinoWorkingCopy.setBoard(board);

		workingCopy.save();
		return true;
	}
	
}
