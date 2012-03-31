package logic.smartbar;

import java.util.Set;

import storage.Priority;


/**
 * @author SongYY
 * Commands involves:
 * 		MARK_COMPLETE
 */
public interface IParseMarkPriorityCommand {
	public Set<Integer> extractTaskNum();
	public Priority extractPriority();
}
