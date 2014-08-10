package ca.cdtdoug.wascana.arduino.ui.serial;

import org.eclipse.tcf.te.runtime.interfaces.properties.IPropertiesContainer;
import org.eclipse.tcf.te.ui.terminals.types.AbstractConnectorType;
import org.eclipse.tm.internal.terminal.provisional.api.ITerminalConnector;
import org.eclipse.tm.internal.terminal.provisional.api.TerminalConnectorExtension;

@SuppressWarnings("restriction")
public class ArduinoSerialConnectorType extends AbstractConnectorType {

	@Override
	public ITerminalConnector createTerminalConnector(IPropertiesContainer properties) {
		ITerminalConnector connector = TerminalConnectorExtension.makeTerminalConnector("ca.cdtdoug.wascana.arduino.connector");
		return connector;
	}

}
