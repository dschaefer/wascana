package ca.cdtdoug.wascana.arduino.core;

import java.io.File;
import java.util.List;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.language.settings.providers.GCCBuiltinSpecsDetector;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;

import ca.cdtdoug.wascana.arduino.core.internal.Activator;
import ca.cdtdoug.wascana.arduino.core.remote.Board;

public class AvrLanguageSettingsProvider extends GCCBuiltinSpecsDetector {

	@Override
	public String getToolchainId() {
		return "ca.cdtdoug.wascana.arduino.toolChain.avr";
	}

	@Override
	protected String getToolOptions(String languageId) {
		String opts = super.getToolOptions(languageId);

		try {
			IConfiguration config = ManagedBuildManager.getConfigurationForDescription(currentCfgDescription);
			Board board = ArduinoProjectGenerator.getBoard(config);
			String mcu = board.getMCU();
			if (mcu != null) {
				opts += " -mmcu=" + mcu;
			}
		} catch (CoreException e) {
			Activator.getPlugin().getLog().log(e.getStatus());
		}

		return opts;
	}
	
	@Override
	protected List<String> parseOptions(String line) {
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			if (line.startsWith(" /arduino/")) {
				File full = new File(ArduinoHome.get().getParentFile(), line.trim());
				return parseOptions(" " + full.getAbsolutePath());
			}
		}

		return super.parseOptions(line);
	}
	
	@Override
	public AvrLanguageSettingsProvider cloneShallow() throws CloneNotSupportedException {
		return (AvrLanguageSettingsProvider) super.cloneShallow();
	}

	@Override
	public AvrLanguageSettingsProvider clone() throws CloneNotSupportedException {
		return (AvrLanguageSettingsProvider) super.clone();
	}

}
