package ca.cdtdoug.wascana.arduino.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.envvar.IBuildEnvironmentVariable;
import org.eclipse.cdt.managedbuilder.envvar.IConfigurationEnvironmentVariableSupplier;
import org.eclipse.cdt.managedbuilder.envvar.IEnvironmentVariableProvider;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;

import ca.cdtdoug.wascana.arduino.core.internal.Activator;
import ca.cdtdoug.wascana.arduino.core.target.ArduinoTarget;
import ca.cdtdoug.wascana.arduino.core.target.Board;

public class EnvVarSupplier implements IConfigurationEnvironmentVariableSupplier {

	private EnvVar arduinoHome;
	private EnvVar path;
	
	private static final String OUTPUT_DIR = "OUTPUT_DIR";
	private static final String BOARD = "BOARD";
	private static final String SERIAL_PORT = "SERIAL_PORT";
	private static final String CYGWIN = "CYGWIN";

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
		File arduinoPath = ArduinoHome.get();

		if (arduinoPath.isDirectory()) {
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

	private IBuildEnvironmentVariable getOutputDir(IConfiguration configuration) {
		EnvVar outputDir = new EnvVar();
		outputDir.name = OUTPUT_DIR;
		outputDir.value = "build/" + configuration.getName();
		return outputDir;
	}

	private IBuildEnvironmentVariable getBoard(IConfiguration configuration) {
		try {
			Board board = ArduinoProjectGenerator.getBoard(configuration);
			if (board == null)
				return null;
			
			EnvVar boardVar = new EnvVar();
			boardVar.name = BOARD;
			boardVar.value = board.getId();
			return boardVar;
		} catch (CoreException e) {
			Activator.getPlugin().getLog().log(e.getStatus());
			return null;
		}
	}

	private IBuildEnvironmentVariable getSerialPort() {
		ArduinoTarget activeTarget = Activator.getTargetRegistry().getActiveTarget();
		if (activeTarget == null)
			return null;
		
		EnvVar serialPortVar = new EnvVar();
		serialPortVar.name = SERIAL_PORT;
		serialPortVar.value = activeTarget.getPortName();
		return serialPortVar;
	}

	private IBuildEnvironmentVariable getCygwin() {
		EnvVar var = new EnvVar();
		var.name = CYGWIN;
		var.value = "nodosfilewarning";
		return var;
	}
	
	@Override
	public IBuildEnvironmentVariable getVariable(String variableName,
			IConfiguration configuration, IEnvironmentVariableProvider provider) {
		if (path != null && variableName.equals(path.name)) {
			return path;
		} else if (arduinoHome != null && variableName.equals(arduinoHome.name)) {
			return arduinoHome;
		} else if (variableName.equals(OUTPUT_DIR)) {
			return getOutputDir(configuration);
		} else if (variableName.equals(BOARD)) {
			return getBoard(configuration);
		} else if (variableName.equals(SERIAL_PORT)) {
			return getSerialPort();
		} else if (variableName.equals(CYGWIN)) {
			return getCygwin();
		}
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

		if (configuration != null) {
			vars.add(getOutputDir(configuration));

			IBuildEnvironmentVariable boardVar = getBoard(configuration);
			if (boardVar != null)
				vars.add(boardVar);
			
			IBuildEnvironmentVariable serialPortVar = getSerialPort();
			if (serialPortVar != null)
				vars.add(serialPortVar);
		}

		if (Platform.getOS().equals(Platform.OS_WIN32))
			vars.add(getCygwin());
		
		return vars.toArray(new IBuildEnvironmentVariable[vars.size()]);
	}

}
