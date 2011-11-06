package cs2103.t14j1.logic.smartbar;

import java.util.Date;

import cs2103.t14j1.logic.Commands;
import cs2103.t14j1.storage.Priority;

public interface IParseAddTaskCommand {
public Commands extractCommand();
	public String extractTaskName();
	public String extractListName();
	public Priority extractPriority();
	public String extractPlace();

	public Date extractStartDate();
	public Long extractStartTime();
	
	public Long extractDuration();
	
	public Date extractEndDate();
	public Long extractEndTime();
	
	public Date extractDeadlineDate();
	public Long extractDeadlineTime();
}
