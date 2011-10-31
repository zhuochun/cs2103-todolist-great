package cs2103.t14j1.logic.events;

import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskLists;

public class DeleteTask extends Event {
    
    Task   task;
    String oldListName;
    
    public void register(Object... objs) {
        if (objs[0] instanceof Integer) {
            int index = (Integer) objs[0];
            task = eventHandler.getTask(index);
        } else {
            task = (Task) objs[0];
        }
        
        oldListName = task.getName();
    }
    
    public void execute() {
        String feedback = null;
        
        
        try {
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
                
                feedback = String.format(eventHandler.getMsg("msg.DELETE_SUCCESS"), "TASK", task.getName());
            } else {
                feedback = String.format(eventHandler.getMsg("msg.DELETE_FAIL"), "TASK");
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