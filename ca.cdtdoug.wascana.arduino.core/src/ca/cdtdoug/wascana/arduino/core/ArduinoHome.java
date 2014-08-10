package ca.cdtdoug.wascana.arduino.core;

import java.io.File;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.Platform;

public class ArduinoHome {

	private static File home;
	
	public static File get() {
		if (home == null) {
			String arduinoPathStr = System.getProperty("ca.cdtdoug.wascana.arduino.home");
			if (arduinoPathStr != null) {
				home = new File(arduinoPathStr);
			} else {
				try {
					home = new File(new File(Platform.getInstallLocation().getURL().toURI()), "arduino");
				} catch (URISyntaxException e) {
					// TODO log
					e.printStackTrace();
					home = new File("nohome");
				}
			}
		}
		return home;
	}

}
