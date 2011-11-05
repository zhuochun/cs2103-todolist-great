package cs2103.t14j1.logic;

/*
 * Importing these interface to support cross-referencing
 */
import cs2103.t14j1.logic.smartbar.ParseCommand;
import cs2103.t14j1.logic.smartbar.IParseAddTaskCommand;
import cs2103.t14j1.logic.smartbar.ParseCommandGetType;
import cs2103.t14j1.logic.smartbar.IParseListRelatedCommand;
import cs2103.t14j1.logic.smartbar.IParseReminderCommand;
import cs2103.t14j1.logic.smartbar.IParseSearchCommand;
import cs2103.t14j1.logic.smartbar.IParseTaskNumRelatedCommand;
import cs2103.t14j1.logic.smartbar.IParseMarkPriorityCommand;

public enum Commands {

    /**
     * Syntax: add + task str
     * @see {@link ParseAddTaskCommand}
     */
    ADD_TASK,

    /**
     * Syntax: del + [num]
     * @see {@link ParseTaskNumRelatedCommand}
     */
    DELETE_TASK,

    /**
     * Syntax: [move|mv] [num] [#list]
     * @see {@link ParseTaskNumRelatedCommand}, {@link ParseListRelatedCommand}
     */
    MOVE_TASK,
    
    /**
     * Syntax: edit + [num]
     * @see {@link ParseTaskNumRelatedCommand}
     */
    EDIT_TASK,
    
    /**
     * Syntax: [num] + done
     * Syntax: done + [num]
     * @see {@link ParseTaskNumRelatedCommand}
     */
    MARK_COMPLETE,
    
    /**
     * Syntax: [num] + [!1~3]
     * @see {@link ParseMarkPriorityCommand}
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
     *  @see {@link ParseReminderCommand}
     */
    ADD_REMINDER,
    
    /**
     * Syntax: remind [id] cancel
     * @see {@link ParseReminderCommand}
     */
    REMOVE_REMINDER,

    /**
     * Syntax: add [#list]
     * @see {@link ParseListRelatedCommand}
     */
    ADD_LIST,

    /**
     * Syntax: edit [#list]
     * @see {@link ParseListRelatedCommand}
     */
    EDIT_LIST,
    
    /**
     * Syntax: rename [#oldListName] [#newListName]
     * @see {@link ParseListRelatedCommand}
     */
    RENAME_LIST,

    /**
     * Syntax: (del|delete) + [#list]
     * @see {@link ParseListRelatedCommand}
     */
    DELETE_LIST,

    /**
     * Syntax: #(list name)
     * @see {@link ParseListRelatedCommand}
     */
    SWITCH_LIST,

    /**
     * Syntax: / + string to search
     * @see {@link ParseSearchCommand},{@link ParseListRelatedCommand}
     */
    SEARCH,

    /**
     * Syntax: dis/display + [#list]
     * @see {@link ParseListRelatedCommand}
     */
    DISPLAY_LISTS,

    /**
     * Syntax: dis/display + [num]
     * @see {@link ParseTaskNumRelatedCommand}
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
    INVALID
}