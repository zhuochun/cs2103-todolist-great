package cs2103.t14j1.logic.events;

import java.util.Date;

import cs2103.t14j1.logic.DateFormat;
import cs2103.t14j1.storage.Task;

public class AddReminder extends Event {
    
    Task task;
    Date remindTime;
    
    public void register(Object... objs) {
        task       = (Task) objs[0];
        remindTime = (Date) objs[1];
    }
    
    public void execute() {
        String feedback = null;
        
        try {
            eventHandler.getReminder().addReminder(remindTime, task);
            
            eventHandler.refreshTasks();
            eventHandler.setModified();
            
            feedback = String.format(eventHandler.getMsg("msg.ADD_REMINDER"), task.getName(), DateFormat.dateToStrShort(remindTime));
        } catch (NullPointerException e) {
            feedback = e.getMessage();
        } catch (IllegalArgumentException e) {
            feedback = e.getMessage();
        }
        
        eventHandler.setStatus(feedback);
    }
    
    public boolean hasUndo() {
        return true;
    }
    
    public Event undo() {
        Event undo = new RemoveReminder();
        undo.setEventLisnter(eventHandler);
        
        undo.register(task);
        undo.execute();
        
        return null;
    }

}
