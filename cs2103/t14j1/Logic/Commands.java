package cs2103.t14j1.logic;

/**
 * @author SongYY
 * 	for putting the documentation
 *  // Task related Commands
 */
public enum Commands {
    
	/**
	 * Syntax: add + task str
	 */
	ADD_TASK,
	
	/**
	 * Syntax: del + [num]
	 */
	DELETE_TASK,	
	
	/**
	 * // TODO: currently didn't implement
	 * move the task from one list to another
	 */
	MOVE_TASK,
	
	/**
	 * This is only in GUI
	 */
	EDIT_TASK,
	
	// List related Commands
	/**
	 * note on 2011-10-9 16:12:32 -- don't necessary to put the add list, 
	 * cuz it's created automatically. Not implemented in command. 
	 * -- Songyy
	 */
	ADD_LIST,

	/**
	 * TODO: currently didn't implement
	 */
	EDIT_LIST,
	
	/**
	 * Syntax: del + [#list]
	 */
	DELETE_LIST,
	
	/**
	 * 	Syntax: #(list name)
	 */
	SWITCH_LIST,
	
	/**
	 * TODO: currently didn't implement
	 */
	SORT,

	/**
	 * Syntax: / + string to search
	 */
	SEARCH,
	
	/**
	 * Syntax: dis/display + [#list]
	 * 	notes on 2011-10-9 16:47:26 -- don't necessary to put it here
	 *  if the user want to display a list, the user simply switch to a list
	 *  using display command; or can simply search for that list.
	 */
	DISPLAY_LISTS,

	/**
	 * Syntax: dis/display + [num]
	 *  note on 2011-10-9 16:46:54
	 *  if the user want to display a task
	 */
	DISPLAY_TASKS,

	/**
	 * When it's to exit
	 */
	EXIT,
	
	/**
	 * When the commend doesn't match any of the Syntax
	 */
	INVALID
}