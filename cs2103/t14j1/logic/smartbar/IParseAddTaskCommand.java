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
	public Integer extractStartTime();
	
	public Integer extractDuration();
	
	public Date extractEndDate();
	public Integer extractEndTime();
	
	public Date extractDeadlineDate();
	public Integer extractDeadlineTime();
}
