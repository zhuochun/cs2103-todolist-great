package cs2103.t14j1.logic.events;

import cs2103.t14j1.storage.Task;

/**
 * a add task event
 * 
 * @author Zhuochun
 * 
 */
public class AddTask extends Event {

    Task task;

    public void register(Object... objs) {
        task = (Task) objs[0];
    }

    public void execute() {
        String feedback;
        boolean success;

        success = eventHandler.getLists().addTask(task.getList(), task);

        if (success) {
            feedback = String.format(eventHandler.getMsg("msg.ADD_TASK_SUCCESS"), task.getName(), task.getList());

            eventHandler.setModified();

            eventHandler.refreshLists();
            eventHandler.switchToTask(task.getList());
        } else {
            feedback = String.format(eventHandler.getMsg("msg.ADD_FAIL"), "Task");
        }

        eventHandler.setStatus(feedback);
    }

    public boolean hasUndo() {
        return true;
    }

    public Event undo() {
        Event undo = new DeleteTask();
        undo.setEventLisnter(eventHandler);

        undo.register(task);
        undo.execute();

        return null;
    }

}
