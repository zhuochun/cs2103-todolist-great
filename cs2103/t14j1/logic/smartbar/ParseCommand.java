package cs2103.t14j1.logic.smartbar;

import java.util.Date;
import java.util.List;

import cs2103.t14j1.logic.Commands;
import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.taskmeter.reminder.Reminder;


public class ParseCommand extends ParseCommandGetType
implements
	ParseAddTaskCommand, ParseListRelatedCommand,ParseTaskNumRelatedCommand,
	ParseMarkPriorityCommand,ParseReminderCommand,ParseSearchCommand
	
{
	public ParseCommand(String command) {
		super(command);
	}

	@Override
	public Date extractSearchBeforeDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date extractSearchAfterDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reminder getRemindParamter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getRemindTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> extractTaskNum() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String extractNewListName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String extractTaskName() {
		return this.sterilizedCommand;
	}

	@Override
	public String extractListName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Priority extractPriority() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String extractPlace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date extractStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long extractStartTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long extractDuration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date extractEndDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long extractEndTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date extractDeadlineDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long extractDeadlineTime() {
		// TODO Auto-generated method stub
		return null;
	}
}