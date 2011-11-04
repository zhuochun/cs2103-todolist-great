package cs2103.t14j1.logic.smartbar;

import cs2103.t14j1.storage.Priority;

/**
 * @author SongYY
 * Commands involves:
 * 		MARK_COMPLETE
 */
public interface ParseMarkPriorityCommand {
	
	public Priority extractPriority();
}
