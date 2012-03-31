package logic.smartbar;

import gui.reminder.Reminder;

import java.util.Date;

import logic.Commands;


public interface IParseReminderCommand {
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
