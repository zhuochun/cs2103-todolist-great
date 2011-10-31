package cs2103.t14j1.logic.events;

import cs2103.t14j1.logic.Commands;
import cs2103.t14j1.taskmeter.EventListener;

public abstract class Event {
    
    protected EventListener eventHandler;
    
    public Event() {
    }
    
    public static Event generateEvent(Commands type) {
        Event newEvent = null;
        
        try {
            switch (type) {
                case ADD_TASK:
                    newEvent = new AddTask();
                    break;
//                case DELETE_TASK:
//                    deleteTask(logic.getTaskIdx());
//                    break;
//                case MOVE_TASK:
//                    moveTask(logic.getTaskIdx(), logic.getListName());
//                    break;
//                case EDIT_TASK:
//                    editTask(logic.getTaskIdx());
//                    break;
//                case ADD_REMINDER:
//                    addReminder(logic.getTaskIdx(), logic.getReminderParameter());
//                    break;
//                case MARK_COMPLETE:
//                    toggleStatus(logic.getTaskIdx());
//                    break;
//                case MARK_PRIORITY:
//                    togglePriority(logic.getTaskIdx(), logic.getNewTaskPriority());
//                    break;
//                case ADD_LIST:
//                    addList(logic.getListName());
//                    break;
//                case EDIT_LIST:
//                    editList(logic.extractNewListName(), null);
//                    break;
//                case RENAME_LIST:
//                    editList(logic.extractNewListName(), logic.extractNewListName());
//                    break;
//                case DELETE_LIST:
//                    deleteList(logic.getListName());
//                    break;
//                case SWITCH_LIST:
//                    switchList(logic.getListName());
//                    break;
//                case SEARCH:
//                    searchResult = logic.getSearchResult();
//                    displaySearchResult();
//                    break;
                default:
                    //setStatusBar(getResourceString("msg.invalid.command"));
                    break;
            }
        } catch (Exception e) {
            //setStatusBar(getResourceString("error.logic.command"));
        }
        
        return newEvent;
    }
    
    public void setEventLisnter(EventListener e) {
        eventHandler = e;
    }
    
    public void register(Object... objs) {
    }
    
    public void execute() {
    }
    
    public boolean hasUndo() {
        return false;
    }
    
    public Event undo() {
        return null;
    }
}
