package ca.cdtdoug.wascana.arduino.ui.remote;

import jssc.SerialPortList;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.remote.core.api2.IRemoteConnectionManager;
import org.eclipse.remote.core.api2.IRemoteConnectionWorkingCopy;
import org.eclipse.remote.core.api2.IRemoteManager;
import org.eclipse.remote.core.api2.IRemoteServices;
import org.eclipse.remote.core.exception.RemoteConnectionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ca.cdtdoug.wascana.arduino.core.remote.Board;
import ca.cdtdoug.wascana.arduino.core.remote.IArduinoBoardManager;
import ca.cdtdoug.wascana.arduino.core.remote.IArduinoRemoteConnection;
import ca.cdtdoug.wascana.arduino.core.remote.IArduinoRemoteConnectionWorkingCopy;
import ca.cdtdoug.wascana.arduino.ui.internal.Activator;

public class NewArduinoTargetWizardPage extends WizardPage {

	String name;
	private Text nameText;

	String portName;
	private String[] portNames;
	private Combo portCombo;

	Board board;
	private Board[] boards;
	private Combo boardCombo;

	public NewArduinoTargetWizardPage() {
		super("NewArduinoTargetPage");
		setDescription("New Arduino Target settings");
		setTitle("New Arduino Target");
	}

	@Override
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));

		Label nameLabel = new Label(comp, SWT.NONE);
		nameLabel.setText("Target name:");

		nameText = new Text(comp, SWT.BORDER | SWT.SINGLE);
		nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		nameText.setText("");
		nameText.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateStatus();
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		Label portLabel = new Label(comp, SWT.NONE);
		portLabel.setText("Serial port:");

		portCombo = new Combo(comp, SWT.READ_ONLY);
		portCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		portNames = SerialPortList.getPortNames();
		for (String portName : portNames) {
			portCombo.add(portName);
		}
		portCombo.select(0);
		portCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateStatus();
			}
		});

		IArduinoBoardManager boardManager = Activator.getService(IArduinoBoardManager.class);
		
		Label boardLabel = new Label(comp, SWT.NONE);
		boardLabel.setText("Board type:");

		boardCombo = new Combo(comp, SWT.READ_ONLY);
		boardCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		boards = boardManager.getBoards().toArray(new Board[0]);
		for (Board board : boards) {
			boardCombo.add(board.getName());
		}
		boardCombo.select(0);
		boardCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateStatus();
			}
		});

		setControl(comp);
		setPageComplete(false);
	}

	private void updateStatus() {
		name = nameText.getText();

		int portIndex = portCombo.getSelectionIndex();
		portName = portIndex < 0 ? null : portNames[portIndex];

		int boardIndex = boardCombo.getSelectionIndex();
		board = boardIndex < 0 ? null : boards[boardIndex];

		setPageComplete(!name.isEmpty() && portName != null && board != null);
	}

	boolean performFinish() {
		IRemoteManager remoteManager = Activator.getService(IRemoteManager.class);
		IRemoteServices remoteServices = remoteManager.getRemoteServices(IArduinoRemoteConnection.TYPE_ID);
		IRemoteConnectionManager connManager = remoteServices.getService(IRemoteConnectionManager.class);
		try {
			IRemoteConnectionWorkingCopy workingCopy = connManager.newConnection(name);
			IArduinoRemoteConnectionWorkingCopy arduinowc = workingCopy.getWorkingCopyService(IArduinoRemoteConnectionWorkingCopy.class);
			arduinowc.setPortName(portName);
			arduinowc.setBoard(board);
			workingCopy.save();
		} catch (RemoteConnectionException e) {
			Activator.getDefault().getLog().log(e.getStatus());
			return false;
		}
		
		return true;
	}
	
}