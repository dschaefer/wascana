package ca.cdtdoug.wascana.arduino.core.target;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;

import ca.cdtdoug.wascana.arduino.core.ArduinoHome;
import ca.cdtdoug.wascana.arduino.core.internal.Activator;

public class ArduinoTargetRegistry {

	private Map<String, Board> boards = new HashMap<>();
	private Map<String, ArduinoTarget> targets = new HashMap<>();
	private List<Listener> listeners = new LinkedList<>();
	private ArduinoTarget activeTarget;

	public interface Listener {
		void targetAdded(ArduinoTarget target);
		void targetRemoved(ArduinoTarget target);
	}

	public ArduinoTargetRegistry() {
		loadBoardFiles();
		loadTargetFiles();
	}

	public Board[] getBoards() {
		Board[] sortedBoards = boards.values().toArray(new Board[boards.size()]);
		Arrays.sort(sortedBoards, new Comparator<Board>() {
			@Override
			public int compare(Board arg0, Board arg1) {
				return arg0.getName().compareTo(arg1.getName());
			}
		});
		return sortedBoards;
	}

	public Board getBoard(String id) {
		return boards.get(id);
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
					boards.put(names[0], new Board(names[0], boardProps));
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

	public ArduinoTarget getTarget(String name) {
		return targets.get(name);
	}

	public void removeTarget(ArduinoTarget target) {
		target.delete();
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

	private void loadTargetFiles() {
		for (File targetFile : getTargetsDir().listFiles()) {
			try { 
				addTarget(new ArduinoTarget(this, targetFile));
			} catch (CoreException e) {
				Activator.getPlugin().getLog().log(e.getStatus());
			}
		}
		
	}

	File getTargetsDir() {
		File stateLocation = Platform.getStateLocation(Activator.getContext().getBundle()).toFile();
		File targetsDir = new File(stateLocation, "targets");
		if (!targetsDir.exists())
			targetsDir.mkdirs();
		return targetsDir;
	}
	
}
