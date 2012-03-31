package logic.events;

import storage.Task;

/**
 * a toggle status event
 * 
 * @author Zhuochun
 *
 */
public class ToggleStatus extends Event {

    int index;
    Task task;
    Boolean oldStatus;
    Boolean newStatus;

    /**
     * index, newStatus (null for toggle)
     * or task, newStatus (null for toggle)
     */
    public void register(Object... objs) {
        if (objs[0] instanceof Integer) {
            task = null;
            index = (Integer) objs[0];
            newStatus = (Boolean) objs[1];
        } else if (objs[0] instanceof Task) {
            index = -1;
            task = (Task) objs[0];
            newStatus = (Boolean) objs[1];
        }
    }

    public boolean execute() {
        String feedback = null;
        boolean success = false;

        try {
            if (task == null) {
                task = eventHandler.getTask(index);
            }

            oldStatus = task.getStatus();

            if (newStatus == null) {
                newStatus = oldStatus == Task.COMPLETED ? Task.INCOMPLETE : Task.COMPLETED;
            }

            task.setStatus(newStatus);

            eventHandler.setModified();
            eventHandler.refreshTasks();

            success  = true;
            feedback = String.format(eventHandler.getMsg("msg.TOGGLE"), task.getName(), task.getStatusStr());
        } catch (IndexOutOfBoundsException e) {
            feedback = e.getMessage();
            success  = false;
        }

        eventHandler.setStatus(feedback);
        return success;
    }

    public boolean hasUndo() {
        return true;
    }

    public Event undo() {
        Event undo = new ToggleStatus();
        undo.setEventLisnter(eventHandler);

        undo.register(task, oldStatus);
        
        boolean success = undo.execute();
        
        if (success) {
            return undo;
        } else {
            return null;
        }
    }
}