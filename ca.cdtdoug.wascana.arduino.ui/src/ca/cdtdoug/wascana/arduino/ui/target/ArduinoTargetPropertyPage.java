package ca.cdtdoug.wascana.arduino.ui.target;

import jssc.SerialPortList;

import org.eclipse.cdt.launchbar.core.ILaunchTarget;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import ca.cdtdoug.wascana.arduino.core.launch.ArduinoLaunchTarget;
import ca.cdtdoug.wascana.arduino.core.target.Board;
import ca.cdtdoug.wascana.arduino.ui.internal.Activator;

public class ArduinoTargetPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

	private String[] ports;
	private Combo portSelector;

	private Board[] boards;
	private Combo boardSelector;

	@Override
	protected Control createContents(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));

		ArduinoLaunchTarget target = (ArduinoLaunchTarget) getElement().getAdapter(ILaunchTarget.class);

		Label portLabel = new Label(comp, SWT.NONE);
		portLabel.setText("Serial Port:");

		portSelector = new Combo(comp, SWT.READ_ONLY);
		portSelector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		String currentPort = target.getTarget().getPortName();
		int i = 0, portSel = 0;
		ports = SerialPortList.getPortNames();
		for (String port : ports) {
			portSelector.add(port);
			if (port.equals(currentPort)) {
				portSel = i;
			}
			i++;
		}
		if (ports.length >= 0) {
			portSelector.select(portSel);
		} else {
			setMessage("No serial ports", ERROR);
			setValid(false);
		}

		Label boardLabel = new Label(comp, SWT.NONE);
		boardLabel.setText("Board type:");

		boardSelector = new Combo(comp, SWT.READ_ONLY);
		boardSelector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Board currentBoard = target.getTarget().getBoard();
		i = 0;
		int boardSel = 0;
		boards = Activator.getTargetRegistry().getBoards();
		for (Board board : boards) {
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
		ArduinoLaunchTarget target = (ArduinoLaunchTarget) getElement().getAdapter(ILaunchTarget.class);

		int i = portSelector.getSelectionIndex();
		if (i >= 0) {
			try {
				target.getTarget().setPortName(ports[i]);
			} catch (CoreException e) {
				Activator.getDefault().getLog().log(e.getStatus());
				return false;
			}
		}

		i = boardSelector.getSelectionIndex();
		try {
			target.getTarget().setBoard(boards[i]);
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(e.getStatus());
			return false;
		}

		return true;
	}

	
}
