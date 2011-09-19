//Shubham: Please also include a function that returns the actual title of the task.
//Looks like you have implemented all other functions but forgotten that.

package cs2103.t14j1.Logic;

/**
 * Use this interface to define all the "extract" function of the parsed 
 * 	information for Smart Bar.
 * 
 * @author Song Yangyu
 */
public interface ExtractParsedCommand {
	/**
	 * 
	 * @return
	 * Extract information from the command
	 * would return null if the information doesn't exist
	 */
	public Commands extractCommand();
	
	/** 
	 * @return
	 * Get the task title
	 * This would exclude other extractable information. For example, if the 
	 *   task is "lunch with Zhuochun 12:00 today at Arts Canteen", then this 
	 *   command would return "lunch with Zhuochun" cuz "12:00", "today", 
	 *   "at Arts Canteen" has been extracted.  
	 */
	public String extractTaskTitle();
	
	/**
	 * @return
	 * 	the timestamp of the start date (at 0:00 time)
	 *  null on doesn't exist 
	 */
	public Long extractStartDate();
	
	/**
	 * @return
	 *  the number of seconds passed from 0:00 of a day.
	 *  null on doesn't exist
	 */
	public Long extractStartTime();
	
	/**
	 * @return 
	 * 	the timestamp of the end date (at 0:00 time)
	 * 	null on doesn't exist
	 */
	public Long extractEndDate();
	
	/**
	 * @return
	 *  the number of seconds passed from 0:00 of a day.
	 *  null on doesn't exist
	 */
	public Long extractEndTime();
	
	/**
	 * @return
	 * 	the number of seconds in that duration 
	 */
	public Long extractDuration();
	
	/**
	 * @return
	 * 	The place as a String.
	 *  null on doesn't exist
	 */
	public String extractPlace();
	
	/**
	 * @return
	 *  A String array, each element is a list member.
	 *   Currently we have one list only therefore it would have one element; but
	 *   I make it array here to make it possible to support multiple list
	 *  Return null if there's no list specified
	 */
	public String[] extractListName();
	
	/**
	 * @return
	 *  the deadline as a timestamp 
	 * 		(would also include the time if time is specified)
	 *  null on doesn't exist
	 */
	public Long extractDeadline();
	
	/**
	 * @return
	 *  the "before" timestamp used in search
	 *  null on doesn't exist
	 */
	public Long extractBeforeTimestamp();
	
	/**
	 * @return
	 *  the "before" timestamp used in search
	 *  null on doesn't exist
	 *  Shubham: I don't understand what this function is for. Please exlain to me on gTalk
	 */
	public Long extractAfterTimestamp();
	
	/**
	 * @return
	 * 	integer indicating priority
	 *  null on doesn't exist
	 *  Shubham: I don't understand what this function is for. Please exlain to me on gTalk
	 */
	public Integer extractPriority();
}
