package ca.cdtdoug.wascana.arduino.ui.serial;

import org.eclipse.tcf.te.runtime.interfaces.properties.IPropertiesContainer;
import org.eclipse.tm.internal.terminal.provisional.api.ISettingsStore;

public class ArduinoSerialSettingsStore implements ISettingsStore {

	private final IPropertiesContainer properties;
	
	public ArduinoSerialSettingsStore(IPropertiesContainer data) {
		this.properties = data;
	}

	public IPropertiesContainer getProperties() {
		return properties;
	}

	@Override
	public String get(String key) {
		return (String)properties.getProperty(key);
	}

	@Override
	public String get(String key, String defaultValue) {
		Object obj = properties.getProperty(key);
		return obj != null ? (String) key : defaultValue;
	}

	@Override
	public void put(String key, String value) {
		properties.setProperty(key, value);
	}

}
