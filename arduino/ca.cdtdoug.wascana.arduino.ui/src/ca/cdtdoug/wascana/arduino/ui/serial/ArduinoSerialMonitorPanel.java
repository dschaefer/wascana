package ca.cdtdoug.wascana.arduino.ui.serial;

import org.eclipse.remote.core.api2.IRemoteConnection;
import org.eclipse.remote.core.api2.IRemoteConnectionManager;
import org.eclipse.remote.core.api2.IRemoteManager;
import org.eclipse.remote.core.api2.IRemoteServices;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.tcf.te.runtime.interfaces.properties.IPropertiesContainer;
import org.eclipse.tcf.te.runtime.services.interfaces.constants.ITerminalsConnectorConstants;
import org.eclipse.tcf.te.ui.controls.BaseDialogPageControl;
import org.eclipse.tcf.te.ui.interfaces.data.IDataExchangeNode;
import org.eclipse.tcf.te.ui.terminals.panels.AbstractConfigurationPanel;
import org.eclipse.ui.forms.widgets.FormToolkit;

import ca.cdtdoug.wascana.arduino.core.remote.IArduinoRemoteConnection;
import ca.cdtdoug.wascana.arduino.ui.internal.Activator;

public class ArduinoSerialMonitorPanel extends AbstractConfigurationPanel implements IDataExchangeNode {

	private Combo targetSelector;
	private IRemoteConnection[] targets;

	public ArduinoSerialMonitorPanel(BaseDialogPageControl parentControl) {
		super(parentControl);
	}

	@Override
	public void setupPanel(Composite parent, FormToolkit toolkit) {
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new GridLayout());
		panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite targetComp = new Composite(panel, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		targetComp.setLayout(layout);
		targetComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label targetLabel = new Label(targetComp, SWT.NONE);
		targetLabel.setText("Arduino Target:");

		IRemoteManager remoteManager = Activator.getService(IRemoteManager.class);
		IRemoteServices remoteServices = remoteManager.getRemoteServices(IArduinoRemoteConnection.TYPE_ID);
		IRemoteConnectionManager connectionManager = remoteServices.getService(IRemoteConnectionManager.class); 
		targets = connectionManager.getConnections().toArray(new IRemoteConnection[0]);
		
		if (targets.length > 0) {
			targetSelector = new Combo(targetComp, SWT.READ_ONLY);
			targetSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	
//			ArduinoRemoteConnection activeTarget = targetRegistry.getActiveTarget();
//			int i = 0, activeTargetIndex = -1;
//			for (ArduinoRemoteConnection target : targets) {
//				targetSelector.add(target.getName());
//				if (target.equals(activeTarget))
//					activeTargetIndex = i;
//				i++;
//			}
//			targetSelector.select(activeTargetIndex);
		} else {
			Label noTarget = new Label(targetComp, SWT.NONE);
			noTarget.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			noTarget.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			noTarget.setText("ERROR: Please add target first.");
			setMessage("Please create Arduino Target first", ERROR);
		}

		setControl(panel);
	}

	@Override
	public boolean isValid() {
		return targetSelector != null;
	}
	
	@Override
	public boolean dataChanged(IPropertiesContainer data, TypedEvent e) {
		return false;
	}

	@Override
	protected void saveSettingsForHost(boolean add) {
		// nothing
	}

	@Override
	protected void fillSettingsForHost(String host) {
		// nothing
	}

	@Override
	protected String getHostFromSettings() {
		// nothing
		return null;
	}

	@Override
	public void setupData(IPropertiesContainer data) {
		// nothing
	}

	public static void extractData(IPropertiesContainer data, IRemoteConnection target) {
		data.setProperty(ArduinoSerialMonitorDelegate.TARGET_NAME, target.getName());

		data.setProperty(ITerminalsConnectorConstants.PROP_TERMINAL_CONNECTOR_ID, ArduinoSerialConnectorType.ID);
		data.setProperty(ITerminalsConnectorConstants.PROP_CONNECTOR_TYPE_ID, ArduinoSerialConnectorType.ID);
		data.setProperty(ITerminalsConnectorConstants.PROP_HAS_DISCONNECT_BUTTON, true);
		data.setProperty(ITerminalsConnectorConstants.PROP_TITLE, target.getName());
	}

	@Override
	public void extractData(IPropertiesContainer data) {
		IRemoteConnection target = targets[targetSelector.getSelectionIndex()];
		extractData(data, target);
	}

}
