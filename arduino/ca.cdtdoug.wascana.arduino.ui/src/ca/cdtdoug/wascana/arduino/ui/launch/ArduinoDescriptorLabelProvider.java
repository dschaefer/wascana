package ca.cdtdoug.wascana.arduino.ui.launch;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.launchbar.core.ILaunchDescriptor;
import org.eclipse.swt.graphics.Image;

import ca.cdtdoug.wascana.arduino.ui.internal.Activator;

public class ArduinoDescriptorLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		return Activator.getDefault().getImage(Activator.IMG_ARDUINO);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof ILaunchDescriptor)
			return ((ILaunchDescriptor) element).getName();
		return super.getText(element);
	}

}
