package cs2103.t14j1.logic;

public enum Commands {
    // Task related Commands
	ADD_TASK,	// Syntax: add + task str
	DELETE_TASK,	// Syntax: del + [num]
	MOVE_TASK,		// TODO: currently didn't implement
	EDIT_TASK,		// Only in GUI
	// List related Commands
	ADD_LIST,
		// note on 2011-10-9 16:12:32 -- don't necessary to put the add list, 
		// cuz it's created automatically. Not implemented in command.
		// -- Songyy
	EDIT_LIST,		// TODO: currently didn't implement
	DELETE_LIST,	// Syntax: del + [#list]
	SWITCH_LIST,	// Syntax: #list name
	// General Commands
	SORT,			// TODO: currently didn't implement
		// 
	SEARCH,			// Syntax: / + string to search
	DISPLAY_LISTS,	// Syntax: dis/display + [#list]
		// notes on 2011-10-9 16:47:26 -- don't necessary to put it here
		// if the user want to display a list, the user simply switch to a list
		// using display command; or can simply search for that list.
	DISPLAY_TASKS,	// Syntax: dis/display + [num]
		// note on 2011-10-9 16:46:54	
		// if the user want to display a task
	INVALID,
	//Exit Command
	EXIT
}