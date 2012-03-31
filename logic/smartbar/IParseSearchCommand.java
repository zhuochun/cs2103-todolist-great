package logic.smartbar;

import java.util.Date;

import storage.Priority;

import logic.Commands;


public interface IParseSearchCommand {
	public Commands extractCommand();
	public String extractTaskName();
	public String extractListName();
	public Priority extractPriority();
	public String extractPlace();
	
	/**
	 * Search would check the time before this time point
	 * @return
	 *  the time point specified by user
	 */
	public Date extractSearchBeforeDate();
	/**
	 * Search would check the time after this time point
	 * @return
	 *  the time point specified by user
	 */
	public Date extractSearchAfterDate();
}
