package ca.cdtdoug.wascana.arduino.ui.serial;

import org.eclipse.tcf.te.runtime.interfaces.callback.ICallback;
import org.eclipse.tcf.te.runtime.interfaces.properties.IPropertiesContainer;
import org.eclipse.tcf.te.runtime.services.ServiceManager;
import org.eclipse.tcf.te.runtime.services.interfaces.ITerminalService;
import org.eclipse.tcf.te.ui.controls.BaseDialogPageControl;
import org.eclipse.tcf.te.ui.terminals.interfaces.IConfigurationPanel;
import org.eclipse.tcf.te.ui.terminals.launcher.AbstractLauncherDelegate;

public class ArduinoSerialMonitorDelegate extends AbstractLauncherDelegate {

	public static final String TARGET_NAME = "arduino.targetName";
	
	@Override
	public boolean needsUserConfiguration() {
		return true;
	}

	@Override
	public IConfigurationPanel getPanel(BaseDialogPageControl parentControl) {
		return new ArduinoSerialMonitorPanel(parentControl);
	}

	@Override
	public void execute(IPropertiesContainer properties, ICallback callback) {
		ITerminalService terminal = ServiceManager.getInstance().getService(ITerminalService.class);
		if (terminal != null) {
			terminal.openConsole(properties, callback);
		}
	}

}
