package cs2103.t14j1.logic;

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
     * Syntax: move [num] [#list]
     */
    MOVE_TASK,
    
    /**
     * Syntax: edit + [num]
     */
    EDIT_TASK,
    
    /**
     * Syntax: [num] + done
     */
    MARK_COMPLETE,
    
    /**
     * Syntax: [num] + [!1~3]
     */
    MARK_PRIORITY,
    
    /**
     * Syntax: remind [id] [start|end|deadline|custom]
     * 
     * start    -> on StartDateTime
     * end      -> on EndDateTime
     * deadline -> on Deadline
     * custom   -> according to user. eg remind 2 5 mins [later], remind 3:23pm etc..
     * 
     */
    ADD_REMINDER,

    /**
     * Syntax: add [#list]
     */
    ADD_LIST,

    /**
     * Syntax: edit [#list]
     */
    EDIT_LIST,
    
    /**
     * Syntax: rename [#oldListName] [#newListName]
     */
    RENAME_LIST,

    /**
     * Syntax: del + [#list]
     */
    DELETE_LIST,

    /**
     * Syntax: #(list name)
     */
    SWITCH_LIST,

    /**
     * Syntax: / + string to search
     */
    SEARCH,

    /**
     * Syntax: dis/display + [#list]
     */
    DISPLAY_LISTS,

    /**
     * Syntax: dis/display + [num]
     */
    DISPLAY_TASK,

    EXIT,

    INVALID
}