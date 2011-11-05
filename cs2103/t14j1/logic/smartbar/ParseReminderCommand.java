package cs2103.t14j1.logic.smartbar;

import java.util.Date;

import cs2103.t14j1.logic.Commands;
import cs2103.t14j1.taskmeter.reminder.Reminder;

public interface ParseReminderCommand {
	public Commands extractCommand();
	
	/** called when it's "rename list"
	 * @return
	 * 	the new list name to be saved.
	 */
	public Reminder getRemindParamter();
	/**
	 * @return
	 *  the time point of reminder
	 */
	public Date getRemindTime();
}
