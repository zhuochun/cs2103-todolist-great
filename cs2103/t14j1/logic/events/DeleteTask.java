package cs2103.t14j1.logic.events;

import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskLists;

/**
 * a delete task event
 * 
 * @author Zhuochun
 * 
 */
public class DeleteTask extends Event {

    int index;
    Task task;
    String oldListName;

    public void register(Object... objs) {
        if (objs[0] instanceof Integer) {
            index = (Integer) objs[0];
            task = null;
        } else if (objs[0] instanceof Task) {
            task = (Task) objs[0];
            index = -1;
        }

        oldListName = task.getName();
    }

    public void execute() {
        String feedback = null;

        try {
            if (index != -1) {
                task = eventHandler.getTask(index);
            }

            boolean success = false;

            // move task to trash, if task is in trash, delete it
            if (task.getList().equals(TaskLists.TRASH)) {
                success = eventHandler.getLists().removeTask(task.getList(), task);
            } else {
                success = eventHandler.getLists().moveTask(TaskLists.TRASH, task);
            }

            if (success) {
                eventHandler.setModified();
                eventHandler.refreshTasks();

                feedback = String.format(eventHandler.getMsg("msg.DELETE_SUCCESS"), "Task", task.getName());
            } else {
                feedback = String.format(eventHandler.getMsg("msg.DELETE_FAIL"), "Task");
            }
        } catch (IndexOutOfBoundsException e) {
            feedback = e.getMessage();
        } catch (Exception e) {
            feedback = e.getMessage();
        }

        eventHandler.setStatus(feedback);
    }

    public boolean hasUndo() {
        return true;
    }

    public Event undo() {
        task.setName(oldListName);

        Event undo = new AddTask();
        undo.setEventLisnter(eventHandler);

        undo.register(task);
        undo.execute();

        return undo;
    }

}
