package ca.cdtdoug.wascana.arduino.ui.target;

import org.eclipse.cdt.launchbar.core.ILaunchTarget;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ca.cdtdoug.wascana.arduino.core.target.ArduinoTarget;
import ca.cdtdoug.wascana.arduino.ui.internal.Activator;

public class ArduinoTargetLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		if (element instanceof ILaunchTarget && ((ILaunchTarget)element).getAdapter(ArduinoTarget.class) != null)
			return Activator.getDefault().getImage(Activator.IMG_ARDUINO);
		return super.getImage(element);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof ILaunchTarget) {
			return ((ILaunchTarget) element).getName();
		}
		return super.getText(element);
	}

}
