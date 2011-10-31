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

    public void register(Object... objs) {
        if (objs[0] instanceof Integer) {
            index = (Integer) objs[0];
            newTask = eventHandler.getTask(index);
            oldTask = (Task) newTask.clone();
        } else if (objs[0] instanceof Task) {
            index = -1;
            oldTask = (Task) objs[0];
            newTask = (Task) objs[1];
        }
    }

    public void execute() {
        if (index == -1) {
            oldTask.copy(newTask);
        } else {
            eventHandler.editTask(index);
        }

        eventHandler.refreshTasks();
    }

    public boolean hasUndo() {
        return true;
    }

    public Event undo() {
        Event undo = new EditTask();
        undo.setEventLisnter(eventHandler);

        undo.register(newTask, oldTask);
        undo.execute();

        return undo;
    }

}