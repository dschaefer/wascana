package ca.cdtdoug.wascana.arduino.ui.serial;

import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tcf.te.runtime.interfaces.properties.IPropertiesContainer;
import org.eclipse.tcf.te.ui.controls.BaseDialogPageControl;
import org.eclipse.tcf.te.ui.terminals.panels.AbstractConfigurationPanel;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class ArduinoSerialMonitorPanel extends AbstractConfigurationPanel {

	public ArduinoSerialMonitorPanel(BaseDialogPageControl parentControl) {
		super(parentControl);
	}

	@Override
	public void setupPanel(Composite parent, FormToolkit toolkit) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean dataChanged(IPropertiesContainer data, TypedEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void saveSettingsForHost(boolean add) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void fillSettingsForHost(String host) {
		// TODO Auto-generated method stub
	}

	@Override
	protected String getHostFromSettings() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
