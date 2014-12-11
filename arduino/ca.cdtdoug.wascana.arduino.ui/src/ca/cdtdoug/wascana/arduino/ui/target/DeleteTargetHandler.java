package ca.cdtdoug.wascana.arduino.ui.target;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.launchbar.core.ILaunchTarget;
import org.eclipse.ui.handlers.HandlerUtil;

import ca.cdtdoug.wascana.arduino.core.target.ArduinoTarget;
import ca.cdtdoug.wascana.arduino.core.target.ArduinoTargetRegistry;
import ca.cdtdoug.wascana.arduino.ui.internal.Activator;

public class DeleteTargetHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			ArduinoTargetRegistry targetRegistry = Activator.getTargetRegistry();
			IStructuredSelection ss = (IStructuredSelection) selection;
			for (Iterator<?> i = ss.iterator(); i.hasNext();) {
				ILaunchTarget launchTarget = (ILaunchTarget) i.next();
				ArduinoTarget target = (ArduinoTarget) launchTarget.getAdapter(ArduinoTarget.class);
				if (target != null)
					targetRegistry.removeTarget(target);
			}
		}
		return null;
	}

}
