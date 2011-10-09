package cs2103.t14j1.logic;

public enum Commands {
    // Task related Commands
	ADD_TASK,	// shubham wants it to be "any commands else"
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
	DISPLAY_TASKS,	// Syntax: dis/display + [num]
	// Invalid Command
	INVALID,		
	//Exit Command
	EXIT
}