package ca.cdtdoug.wascana.arduino.ui.target;

import java.util.Iterator;

import org.eclipse.cdt.launchbar.core.ILaunchTarget;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.tcf.te.runtime.interfaces.properties.IPropertiesContainer;
import org.eclipse.tcf.te.runtime.properties.PropertiesContainer;
import org.eclipse.ui.handlers.HandlerUtil;

import ca.cdtdoug.wascana.arduino.core.target.ArduinoTarget;
import ca.cdtdoug.wascana.arduino.ui.serial.ArduinoSerialMonitorDelegate;
import ca.cdtdoug.wascana.arduino.ui.serial.ArduinoSerialMonitorPanel;

public class OpenTerminalHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			for (Iterator<?> i = ss.iterator(); i.hasNext();) {
				ILaunchTarget launchTarget = (ILaunchTarget) i.next();
				ArduinoTarget target = (ArduinoTarget) launchTarget.getAdapter(ArduinoTarget.class);
				IPropertiesContainer data = new PropertiesContainer();
				ArduinoSerialMonitorPanel.extractData(data, target);
				new ArduinoSerialMonitorDelegate().execute(data, null);
			}
		}
		
		return Status.OK_STATUS;
	}

}
