package cs2103.t14j1.logic.events;

import cs2103.t14j1.storage.Task;

public class EditTask extends Event {
    
    int  index;
    Task oldTask;
    Task newTask;
    
    public void register(Object... objs) {
        if (objs[0] instanceof Integer) {
            index   = (Integer) objs[0];
            newTask = eventHandler.getTask(index);
            oldTask = (Task) newTask.clone();
        } else if (objs[0] instanceof Task) {
            index   = -1;
            oldTask = (Task) objs[0];
            newTask = (Task) objs[1];
        }
    }
    
    public void execute() {
        if (index == -1) {
            copyTask(oldTask, newTask);
        } else {
            eventHandler.editTask(index);
        }
    }
    
    public void copyTask(Task to, Task from) {
        // TODO
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