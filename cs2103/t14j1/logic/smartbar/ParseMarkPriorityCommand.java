package cs2103.t14j1.logic.smartbar;

import java.util.List;

import cs2103.t14j1.storage.Priority;

/**
 * @author SongYY
 * Commands involves:
 * 		MARK_COMPLETE
 */
public interface ParseMarkPriorityCommand {
	public List<Integer> extractTaskNum();
	public Priority extractPriority();
}
