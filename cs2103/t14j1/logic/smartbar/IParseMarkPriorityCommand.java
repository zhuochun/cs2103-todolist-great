package cs2103.t14j1.logic.smartbar;

import java.util.Set;

import cs2103.t14j1.storage.Priority;

/**
 * @author SongYY
 * Commands involves:
 * 		MARK_COMPLETE
 */
public interface IParseMarkPriorityCommand {
	public Set<Integer> extractTaskNum();
	public Priority extractPriority();
}
