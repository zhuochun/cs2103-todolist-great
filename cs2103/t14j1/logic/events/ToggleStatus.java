package cs2103.t14j1.logic.events;

import cs2103.t14j1.storage.Task;

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

    public void execute() {
        String feedback = null;

        try {
            if (index != -1) {
                task = eventHandler.getTask(index);
            }

            oldStatus = task.getStatus();

            if (newStatus == null) {
                newStatus = oldStatus == Task.COMPLETED ? Task.INCOMPLETE : Task.COMPLETED;
            }

            task.setStatus(newStatus);

            eventHandler.setModified();
            eventHandler.refreshTasks();

            feedback = String.format(eventHandler.getMsg("msg.TOGGLE"), task.getName(), task.getStatusStr());
        } catch (IndexOutOfBoundsException e) {
            feedback = e.getMessage();
        }

        eventHandler.setStatus(feedback);
    }

    public boolean hasUndo() {
        return true;
    }

    public Event undo() {
        Event undo = new ToggleStatus();
        undo.setEventLisnter(eventHandler);

        undo.register(task, oldStatus);
        undo.execute();

        return null;
    }
}