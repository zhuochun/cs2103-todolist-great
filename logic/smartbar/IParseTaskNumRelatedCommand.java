package logic.smartbar;

import java.util.Set;

import logic.Commands;


/**
 * Interface for all the commands related to task number. Involves:
 * 		MOVE_TASK,	DELETE_TASK,	EDIT_TASK,		DISPLAY_TASK,
 * 		MARK_COMPLETE
 * @author SongYY
 *
 */
public interface IParseTaskNumRelatedCommand {
	public Commands extractCommand();
	public Set<Integer> extractTaskNum();
}
