package cs2103.t14j1.logic.events;

import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskLists;

/**
 * a move task event
 * 
 * @author Zhuochun
 * 
 */
public class MoveTask extends Event {

    int index;
    Task task;
    String oldListName;
    String newListName;

    public void register(Object... objs) {
        if (objs[0] instanceof Integer) {
            index = (Integer) objs[0];
            task = null;
        } else if (objs[0] instanceof Task) {
            task = (Task) objs[0];
            index = -1;
        }

        newListName = (String) objs[2];
        oldListName = task.getList();
    }

    public void execute() {
        String feedback = null;

        try {
            if (index != -1) {
                task = eventHandler.getTask(index);
            }

            TaskLists lists = eventHandler.getLists();

            lists.moveTask(newListName, task);

            eventHandler.setModified();
            eventHandler.refreshAll();

            feedback = String.format(eventHandler.getMsg("msg.MOVE"), task.getName(), newListName);
        } catch (IllegalArgumentException e) {
            feedback = e.getMessage();
        } catch (NullPointerException e) {
            feedback = e.getMessage();
        } catch (IndexOutOfBoundsException e) {
            feedback = e.getMessage();
        }

        eventHandler.setStatus(feedback);
    }

    public boolean hasUndo() {
        return true;
    }

    public Event undo() {
        Event undo = new MoveTask();
        undo.setEventLisnter(eventHandler);

        undo.register(task, oldListName);
        undo.execute();

        return null;
    }

}
