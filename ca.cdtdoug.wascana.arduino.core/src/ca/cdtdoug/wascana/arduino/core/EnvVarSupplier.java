package ca.cdtdoug.wascana.arduino.core;

import java.io.File;
import java.net.URISyntaxException;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.envvar.IBuildEnvironmentVariable;
import org.eclipse.cdt.managedbuilder.envvar.IConfigurationEnvironmentVariableSupplier;
import org.eclipse.cdt.managedbuilder.envvar.IEnvironmentVariableProvider;
import org.eclipse.core.runtime.Platform;

public class EnvVarSupplier implements IConfigurationEnvironmentVariableSupplier {

	private IBuildEnvironmentVariable path;
	
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
			File avrPath = new File(arduinoPath, "hardware/tools/avr/bin");
			String pathStr = avrPath.getAbsolutePath();
			
			if (Platform.getOS().equals(Platform.OS_WIN32)) {
				// Windows needs the arduino path too to pick up the cygwin dlls
				pathStr += File.pathSeparator + arduinoPath.getAbsolutePath();
			}
			
			final String pathStrFinal = pathStr;
			
			path = new IBuildEnvironmentVariable() {
				@Override
				public String getName() {
					return "PATH";
				}
				
				@Override
				public String getValue() {
					return pathStrFinal;
				}
				
				@Override
				public int getOperation() {
					return IBuildEnvironmentVariable.ENVVAR_PREPEND;
				}
				
				@Override
				public String getDelimiter() {
					return File.pathSeparator;
				}
			};
		}
	}
	
	@Override
	public IBuildEnvironmentVariable getVariable(String variableName,
			IConfiguration configuration, IEnvironmentVariableProvider provider) {
		if (path != null && variableName.equals("PATH"))
			return path;
		return null;
	}

	@Override
	public IBuildEnvironmentVariable[] getVariables(
			IConfiguration configuration, IEnvironmentVariableProvider provider) {
		return path != null ? new IBuildEnvironmentVariable[] { path } : null;
	}

}
