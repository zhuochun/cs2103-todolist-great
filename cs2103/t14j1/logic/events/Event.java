package cs2103.t14j1.logic.events;

import cs2103.t14j1.logic.Commands;
import cs2103.t14j1.taskmeter.EventListener;

/**
 * an abstract class for events
 * 
 * @author Zhuochun
 * 
 */
public abstract class Event {

    protected EventListener eventHandler;

    public Event() {
    }

    public static Event generateEvent(Commands type) {
        Event newEvent = null;

        switch (type) {
            case ADD_TASK:
                newEvent = new AddTask();
                break;
            case DELETE_TASK:
                newEvent = new DeleteTask();
                break;
            case MOVE_TASK:
                newEvent = new MoveTask();
                break;
            case EDIT_TASK:
                newEvent = new EditTask();
                break;
            case ADD_REMINDER:
                newEvent = new AddReminder();
                break;
            case REMOVE_REMINDER:
                newEvent = new RemoveReminder();
                break;
            case MARK_COMPLETE:
                newEvent = new ToggleStatus();
                break;
            case MARK_PRIORITY:
                newEvent = new TogglePriority();
                break;
            case ADD_LIST:
                newEvent = new AddList();
                break;
            case EDIT_LIST:
                newEvent = new EditList();
                break;
            case RENAME_LIST:
                newEvent = new EditList();
                break;
            case DELETE_LIST:
                newEvent = new DeleteList();
                break;
            case SEARCH:
                newEvent = new Search();
                break;
            case BULK:
                newEvent = new BulkEvents();
                break;
            default:
                break;
        }

        return newEvent;
    }

    public void setEventLisnter(EventListener e) {
        eventHandler = e;
    }

    public void register(Object... objs) {
    }

    public boolean execute() {
        return false;
    }

    public boolean hasUndo() {
        return false;
    }

    public Event undo() {
        return null;
    }
}
