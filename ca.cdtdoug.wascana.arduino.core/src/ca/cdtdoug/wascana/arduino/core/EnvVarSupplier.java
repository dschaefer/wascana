package ca.cdtdoug.wascana.arduino.core;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.envvar.IBuildEnvironmentVariable;
import org.eclipse.cdt.managedbuilder.envvar.IConfigurationEnvironmentVariableSupplier;
import org.eclipse.cdt.managedbuilder.envvar.IEnvironmentVariableProvider;
import org.eclipse.core.runtime.Platform;

public class EnvVarSupplier implements IConfigurationEnvironmentVariableSupplier {

	private EnvVar arduinoHome;
	private EnvVar path;
	
	private static final class EnvVar implements IBuildEnvironmentVariable {
		String name;
		String value;
		int operation = IBuildEnvironmentVariable.ENVVAR_REPLACE;
		String delimiter = null;
		
		@Override
		public String getName() {
			return name;
		}
		@Override
		public String getValue() {
			return value;
		}
		@Override
		public int getOperation() {
			return operation;
		}
		@Override
		public String getDelimiter() {
			return delimiter;
		}
	}

	public EnvVarSupplier() {
		File arduinoPath = null;
		String arduinoPathStr = System.getProperty("ca.cdtdoug.wascana.arduino.home");
		if (arduinoPathStr != null) {
			arduinoPath = new File(arduinoPathStr);
		} else {
			try {
				arduinoPath = new File(new File(Platform.getInstallLocation().getURL().toURI()), "arduino");
			} catch (URISyntaxException e) {
				// TODO log
				e.printStackTrace();
			}
		}

		if (arduinoPath != null && arduinoPath.isDirectory()) {
			arduinoHome = new EnvVar();
			arduinoHome.name = "ARDUINO_HOME";
			arduinoHome.value = arduinoPath.getAbsolutePath();

			File avrPath = new File(arduinoPath, "hardware/tools/avr/bin");
			String pathStr = avrPath.getAbsolutePath();
			if (Platform.getOS().equals(Platform.OS_WIN32)) {
				// Windows needs the arduino path too to pick up the cygwin dlls
				pathStr += File.pathSeparator + arduinoPath.getAbsolutePath();
			}
			
			path = new EnvVar();
			path.name = "PATH";
			path.value = pathStr;
			path.operation = IBuildEnvironmentVariable.ENVVAR_PREPEND;
			path.delimiter = File.pathSeparator;
		}
	}
	
	@Override
	public IBuildEnvironmentVariable getVariable(String variableName,
			IConfiguration configuration, IEnvironmentVariableProvider provider) {
		if (path != null && variableName.equals(path.name))
			return path;
		else if (arduinoHome != null && variableName.equals(arduinoHome.name))
			return arduinoHome;
		return null;
	}

	@Override
	public IBuildEnvironmentVariable[] getVariables(
			IConfiguration configuration, IEnvironmentVariableProvider provider) {
		List<IBuildEnvironmentVariable> vars = new ArrayList<>();
		
		if (path != null)
			vars.add(path);
		
		if (arduinoHome != null)
			vars.add(arduinoHome);
		
		return vars.toArray(new IBuildEnvironmentVariable[vars.size()]);
	}

}
