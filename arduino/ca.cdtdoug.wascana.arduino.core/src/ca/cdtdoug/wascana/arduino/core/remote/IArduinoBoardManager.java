package ca.cdtdoug.wascana.arduino.core.remote;

import java.util.Collection;

public interface IArduinoBoardManager {

	Board getBoard(String id);

	Collection<Board> getBoards();

}
