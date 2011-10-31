package cs2103.t14j1.logic.events;

import cs2103.t14j1.logic.Commands;
import cs2103.t14j1.storage.Task;

public class EditTask extends Event {
    
    Task oldTask;
    Task newTask;
    
    public void register(Object... objs) {
        int index = objs[0];
        oldTask = (Task) objs[0];
        newTask = (Task) objs[1];
    }
    
    public void execute() {
        Task task    = eventHandler.getTask(getTaskIdx());
        Task oldTask = (Task) task.clone();
        
        eventHandler.editTask(getTaskIdx());
        
        Event newEvent = Event.generateEvent(Commands.EDIT_TASK);
        registerEvent(newEvent, oldTask, task);
    }
    
    public boolean hasUndo() {
        return true;
    }
    
    public Event undo() {
        return null;
    }

}