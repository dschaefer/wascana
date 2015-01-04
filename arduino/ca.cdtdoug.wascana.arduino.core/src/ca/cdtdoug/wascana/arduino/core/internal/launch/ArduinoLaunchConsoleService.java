package ca.cdtdoug.wascana.arduino.core.internal.launch;


public interface ArduinoLaunchConsoleService {

	/**
	 * Capture the output for the process and display on the console.
	 * 
	 * @param process
	 */
	void monitor(Process process);

}
