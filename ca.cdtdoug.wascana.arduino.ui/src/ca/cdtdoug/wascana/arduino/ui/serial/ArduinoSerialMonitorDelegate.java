package ca.cdtdoug.wascana.arduino.ui.serial;

import org.eclipse.tcf.te.runtime.interfaces.callback.ICallback;
import org.eclipse.tcf.te.runtime.interfaces.properties.IPropertiesContainer;
import org.eclipse.tcf.te.ui.controls.BaseDialogPageControl;
import org.eclipse.tcf.te.ui.terminals.interfaces.IConfigurationPanel;
import org.eclipse.tcf.te.ui.terminals.interfaces.ILauncherDelegate;
import org.eclipse.tcf.te.ui.terminals.launcher.AbstractLauncherDelegate;

public class ArduinoSerialMonitorDelegate extends AbstractLauncherDelegate
		implements ILauncherDelegate {

	@Override
	public boolean needsUserConfiguration() {
		return true;
	}

	@Override
	public IConfigurationPanel getPanel(BaseDialogPageControl parentControl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(IPropertiesContainer properties, ICallback callback) {
		// TODO Auto-generated method stub
		
	}

}
