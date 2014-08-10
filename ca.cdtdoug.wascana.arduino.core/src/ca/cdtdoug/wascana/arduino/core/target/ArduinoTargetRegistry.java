package ca.cdtdoug.wascana.arduino.core.target;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import ca.cdtdoug.wascana.arduino.core.ArduinoHome;

public class ArduinoTargetRegistry {

	private List<Board> boards = new ArrayList<>();
	private Map<String, ArduinoTarget> targets = new HashMap<>();
	private List<Listener> listeners = new LinkedList<>();
	private ArduinoTarget activeTarget;

	public interface Listener {
		void targetAdded(ArduinoTarget target);
		void targetRemoved(ArduinoTarget target);
	}

	public ArduinoTargetRegistry() {
		loadBoardFiles();
	}

	public Board[] getBoards() {
		return boards.toArray(new Board[boards.size()]);
	}

	private void loadBoardFiles() {
		File home = ArduinoHome.get();
		if (!home.isDirectory())
			return;

		File archRoot = new File(home, "hardware/arduino");
		for (File archDir : archRoot.listFiles()) {
			File boardFile = new File(archDir, "boards.txt");
			loadBoardFile(archDir.getName(), boardFile);
		}
	}

	private void loadBoardFile(String arch, File boardFile) {
		try {
			Properties boardProps = new Properties();
			boardProps.load(new FileInputStream(boardFile));
			Enumeration<?> i = boardProps.propertyNames();
			while (i.hasMoreElements()) {
				String propertyName = (String) i.nextElement();
				String[] names = propertyName.split("\\.");
				if (names.length == 2 && names[1].equals("name")) {
					boards.add(new Board(names[0], boardProps));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addTarget(ArduinoTarget target) {
		targets.put(target.getName(), target);
		for (Listener listener : listeners) {
			listener.targetAdded(target);
		}
	}

	public void removeTarget(ArduinoTarget target) {
		targets.remove(target.getName());
		for (Listener listener : listeners) {
			listener.targetRemoved(target);
		}
	}

	public ArduinoTarget[] getTargets() {
		return targets.values().toArray(new ArduinoTarget[targets.size()]);
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public void setActiveTarget(ArduinoTarget target) {
		activeTarget = target;
	}

	public ArduinoTarget getActiveTarget() {
		return activeTarget;
	}

}
