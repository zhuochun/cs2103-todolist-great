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

    MOVE_TASK,

    EDIT_TASK,

    ADD_LIST,

    EDIT_LIST,

    /**
     * Syntax: del + [#list]
     */
    DELETE_LIST,

    /**
     * Syntax: #(list name)
     */
    SWITCH_LIST,

    SORT,

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