package cs2103.t14j1.logic.events;

import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskLists;

public class DeleteTask extends Event {
    
    public void register(Object... objs) {
    }
    
    public void execute() {
        String feedback = null;
        
        
        try {
            Task delTask = getIndexedTask(index);
            
            boolean success = false;
            
            // move task to trash, if task is in trash, delete it
            if (delTask.getList().equals(TaskLists.TRASH)) {
                success = lists.removeTask(delTask.getList(), delTask);
            } else {
                success = lists.moveTask(TaskLists.TRASH, delTask);
            }
            
            if (success) {
                feedback = String.format(getResourceString("msg.DELETE_SUCCESS"), "TASK", delTask.getName());
                isModified = true;
                displayTasks();
            } else {
                feedback = String.format(getResourceString("msg.DELETE_FAIL"), "TASK");
            }
        } catch (IndexOutOfBoundsException e) {
            feedback = e.getMessage();
        } catch (Exception e) {
            feedback = e.getMessage();
        }
        
        setStatusBar(feedback);

    }
    
    public boolean hasUndo() {
        return false;
    }
    
    public Event undo() {
        return null;
    }
    
}
