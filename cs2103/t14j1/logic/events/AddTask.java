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
    String listname;

    public void register(Object... objs) {
        task = (Task) objs[0];
        listname = null;
    }

    public void execute() {
        String feedback;
        boolean success;
        
        if (!eventHandler.getLists().hasList(task.getList())) {
            listname = task.getList();
        }

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
        // TODO: AddTask undo is not exactly as DeleteTask.
        //       eg the undo addTask should delete the addTask, instead of putting it in Trash
        //       eg if addTask created a newList, undo should remove the list as well
        Event undo = new DeleteTask();
        undo.setEventLisnter(eventHandler);

        undo.register(task);
        undo.execute();

        return null;
    }

}
