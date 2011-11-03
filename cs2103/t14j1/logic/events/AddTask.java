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
        assert(task != null);
        
        task = (Task) objs[0];
        listname = null;
    }

    public boolean execute() {
        String feedback;
        boolean success = false;
        
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
        return success;
    }

    public boolean hasUndo() {
        return true;
    }

    public Event undo() {
        // if addTask created a newList, undo should remove the list
        if (listname != null) {
            Event del = new DeleteList();
            del.setEventLisnter(eventHandler);
            del.register(listname);
            
            if (!del.execute()) {
                return null;
            }
        }
        
        Event undo = new DeleteTask();
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
