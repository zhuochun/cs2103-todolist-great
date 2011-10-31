package cs2103.t14j1.logic.events;

import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.storage.Task;

/**
 * a toggle priority event
 * 
 * @author Zhuochun
 *
 */
public class TogglePriority extends Event {

    int index;
    Task task;
    Priority oldPriority;
    Priority newPriority;

    public void register(Object... objs) {
        if (objs[0] instanceof Integer) {
            task = null;
            index = (Integer) objs[0];
        } else if (objs[0] instanceof Task) {
            index = -1;
            task = (Task) objs[0];
        }

        newPriority = (Priority) objs[1];
    }

    public boolean execute() {
        String feedback = null;
        boolean success = false;

        try {
            task = eventHandler.getTask(index);

            oldPriority = task.getPriority();
            task.setPriority(newPriority);

            eventHandler.setModified();
            eventHandler.refreshTasks();

            success  = true;
            feedback = String.format(eventHandler.getMsg("msg.TOGGLE"), task.getName(), newPriority);
        } catch (IndexOutOfBoundsException e) {
            success  = false;
            feedback = e.getMessage();
        }

        eventHandler.setStatus(feedback);
        return success;
    }

    public boolean hasUndo() {
        return true;
    }

    public Event undo() {
        Event undo = new TogglePriority();
        undo.setEventLisnter(eventHandler);

        undo.register(task, oldPriority);
        
        boolean success = undo.execute();
        
        if (success) {
            return undo;
        } else {
            return null;
        }
    }

}