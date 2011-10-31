package cs2103.t14j1.logic.events;

import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.storage.Task;

public class TogglePriority extends Event {
    
    int      index;
    Task     task;
    Priority oldPriority;
    Priority newPriority;
    
    public void register(Object... objs) {
        if (objs[0] instanceof Integer) {
            task        = null;
            index       = (Integer) objs[0];
        } else if (objs[0] instanceof Task) {
            index     = -1;
            task      = (Task)    objs[0];
        }
        
        newPriority = (Priority) objs[1];
    }
    
    public void execute() {
        String feedback = null;
        
        try {
            task = eventHandler.getTask(index);
            
            oldPriority = task.getPriority();
            task.setPriority(newPriority);
            
            eventHandler.setModified();
            eventHandler.refreshTasks();
            
            feedback = String.format(eventHandler.getMsg("msg.TOGGLE"), task.getName(), newPriority);
        } catch (IndexOutOfBoundsException e) {
            feedback = e.getMessage();
        }
        
        eventHandler.setStatus(feedback);
    }
    
    public boolean hasUndo() {
        return true;
    }
    
    public Event undo() {
        Event undo = new TogglePriority();
        undo.setEventLisnter(eventHandler);
        
        undo.register(task, oldPriority);
        undo.execute();
        
        return undo;
    }

}
