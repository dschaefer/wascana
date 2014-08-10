package ca.cdtdoug.wascana.arduino.core.target;

import java.util.Properties;

public class Board {

	private final String key;
	private final Properties properties;

	public Board(String key, Properties properties) {
		this.key = key + ".";
		this.properties = properties;
	}

	public String getProperty(String localKey) {
		return properties.getProperty(key + localKey);
	}

	public String getName() {
		return getProperty("name");
	}

}
