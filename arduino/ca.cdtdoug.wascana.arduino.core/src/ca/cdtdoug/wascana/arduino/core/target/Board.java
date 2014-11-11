package ca.cdtdoug.wascana.arduino.core.target;

import java.util.Properties;

public class Board {

	private final String id;
	private final Properties properties;

	public Board(String key, Properties properties) {
		this.id = key;
		this.properties = properties;
	}

	public String getId() {
		return id;
	}

	public String getProperty(String localKey) {
		return properties.getProperty(id + '.' + localKey);
	}

	public String getName() {
		return getProperty("name");
	}

	public String getMCU() {
		return getProperty("build.mcu");
	}

}
