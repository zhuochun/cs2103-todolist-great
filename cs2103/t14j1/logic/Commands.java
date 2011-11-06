package cs2103.t14j1.logic;

/*
 * Importing these interface to support cross-referencing
 */
import cs2103.t14j1.logic.smartbar.ParseCommand;
import cs2103.t14j1.logic.smartbar.ParseCommandGetType;

/**
 * Defines the different types of commands supported by us
 * @author Shubham Goyal
 *
 */
public enum Commands {

    /**
     * Syntax: add + task str
     * @see {@link IParseAddTaskCommand}
     */
    ADD_TASK,

    /**
     * Syntax: del + [num]
     * @see {@link IParseTaskNumRelatedCommand}
     */
    DELETE_TASK,

    /**
     * Syntax: [move|mv] [num] [#list]
     * @see {@link IParseTaskNumRelatedCommand}, {@link IParseListRelatedCommand}
     */
    MOVE_TASK,
    
    /**
     * Syntax: edit + [num]
     * @see {@link IParseTaskNumRelatedCommand}
     */
    EDIT_TASK,
    
    /**
     * Syntax: [num] + done
     * Syntax: done + [num]
     * @see {@link IParseTaskNumRelatedCommand}
     */
    MARK_COMPLETE,
    
    /**
     * Syntax: [num] + [!1~3]
     * @see {@link IParseMarkPriorityCommand}
     */
    MARK_PRIORITY,
    
    /**
     * Syntax: remind [id] [start|end|deadline|custom]
     * 
     * start    -> on StartDateTime
     * end      -> on EndDateTime
     * deadline -> on Deadline
     * custom   -> supported custom type:
     * 	-> in + time period: e.g.: in 2 hours, in 2 second, in 5 minutes
     *  -> date/time format: a specific date/time point
     *  
     *  @see {@link IParseReminderCommand}
     */
    ADD_REMINDER,
    
    /**
     * Syntax: remind [id] cancel
     * @see {@link IParseReminderCommand}
     */
    REMOVE_REMINDER,

    /**
     * Syntax: add [#list]
     * @see {@link IParseListRelatedCommand}
     */
    ADD_LIST,

    /**
     * Syntax: edit [#list]
     * @see {@link IParseListRelatedCommand}
     */
    EDIT_LIST,
    
    /**
     * Syntax: rename [#oldListName] [#newListName]
     * @see {@link IParseListRelatedCommand}
     */
    RENAME_LIST,

    /**
     * Syntax: (del|delete) + [#list]
     * @see {@link IParseListRelatedCommand}
     */
    DELETE_LIST,

    /**
     * Syntax: #(list name)
     * @see {@link IParseListRelatedCommand}
     */
    SWITCH_LIST,

    /**
     * Syntax: / + string to search
     * @see {@link IParseSearchCommand},{@link IParseListRelatedCommand}
     */
    SEARCH,

    /**
     * Syntax: dis/display + [#list]
     * @see {@link IParseListRelatedCommand}
     */
    DISPLAY_LISTS,

    /**
     * Syntax: dis/display + [num]
     * @see {@link IParseTaskNumRelatedCommand}
     */
    DISPLAY_TASK,
    
    /**
     * Syntax: undo
     * @see {@link ParseCommandGetType}
     */
    UNDO,
    
    /**
     * Syntax: redo
     * @see {@link ParseCommandGetType}
     */
    REDO,
    
    
    /**
     * Syntax: exit
     * @see {@link ParseCommandGetType}
     */
    EXIT,

    /**
     * Other cases
     * @see {@link ParseCommand}
     */
    INVALID,
    
    /**
     * dummy command, used to in events for commands that support
     * multiple index
     */
    BULK;
}