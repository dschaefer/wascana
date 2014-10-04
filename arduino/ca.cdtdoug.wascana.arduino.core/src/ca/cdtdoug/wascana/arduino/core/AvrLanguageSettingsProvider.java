package ca.cdtdoug.wascana.arduino.core;

import org.eclipse.cdt.managedbuilder.language.settings.providers.GCCBuiltinSpecsDetector;

public class AvrLanguageSettingsProvider extends GCCBuiltinSpecsDetector {

	@Override
	public String getToolchainId() {
		return "ca.cdtdoug.wascana.arduino.toolChain.avr";
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
