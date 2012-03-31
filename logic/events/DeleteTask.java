package logic.events;

import storage.Task;
import storage.TaskLists;

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
    }

    public boolean execute() {
        String feedback = null;
        boolean success = false;

        try {
            if (index != -1) {
                task = eventHandler.getTask(index);
                // delete task from search result
                eventHandler.removeTaskInSearch(index);
            }
            oldListName = task.getList();
            
            success = false;

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
            success  = false;
        } catch (Exception e) {
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
        // If the task is deleted to Trash, we just move it back to oldList
        if (!oldListName.equals(TaskLists.TRASH)) {
            Event undo = new MoveTask();
            undo.setEventLisnter(eventHandler);
            
            undo.register(task, oldListName);
            
            boolean success = undo.execute();

            if (success) {
                return undo;
            } else {
                return null;
            }
        } else { // If the task is deleted from Trash, move it back to Trash
            Event undo = new AddTask();
            undo.setEventLisnter(eventHandler);

            undo.register(task);

            boolean success = undo.execute();

            if (success) {
                return undo;
            } else {
                return null;
            }
        }
    }

}
