package cs2103.t14j1.logic.events;

import cs2103.t14j1.storage.Task;

/**
 * a edit task event
 * 
 * @author Zhuochun
 * 
 */
public class EditTask extends Event {

    int index;
    Task oldTask;
    Task newTask;

    /**
     * edit: index
     * undo edit: oldTask newTask
     */
    public void register(Object... objs) {
        if (objs[0] instanceof Integer) {
            index = (Integer) objs[0];
            oldTask = null;
            newTask = null;
        } else if (objs[0] instanceof Task) {
            index = -1;
            oldTask = (Task) objs[0];
            newTask = (Task) objs[1];
        }
    }

    public void execute() {
        try {
            if (index == -1) {
                Task tempTask = (Task) oldTask.clone();
                
                oldTask.copy(newTask); // recover from oldTask
                
                // Swap oldTask, newTask
                newTask = oldTask;
                oldTask = tempTask;
                
                eventHandler.setStatus("Task edit is undo successfully");
            } else {
                newTask = eventHandler.getTask(index);
                oldTask = (Task) newTask.clone();

                eventHandler.editIdxTask(index);
            }

            eventHandler.refreshTasks();
        } catch (IndexOutOfBoundsException e) {
            eventHandler.setStatus(e.getMessage());
        }
    }

    public boolean hasUndo() {
        return true;
    }

    public Event undo() {
        Event undo = new EditTask();
        undo.setEventLisnter(eventHandler);

        undo.register(newTask, oldTask); // undo: recover oldTask
        undo.execute();

        return undo;
    }

}