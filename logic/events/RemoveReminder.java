package logic.events;

import java.util.Date;

import storage.Task;


/**
 * a remove Reminder event
 * 
 * @author Zhuochun
 * 
 */
public class RemoveReminder extends Event {

    Task task;
    Date remindTime;

    public void register(Object... objs) {
        assert(task != null);
        
        task = (Task) objs[0];
    }

    public boolean execute() {
        String feedback = null;
        boolean success = false;

        try {
            remindTime = task.getReminder();
            
            success = eventHandler.getReminder().removeReminder(task);

            if (success) {
                eventHandler.setModified();
                eventHandler.refreshTasks();

                feedback = String.format("Reminder for task \"%1$s\" is successfully removed", task.getName());
            } else {
                feedback = "Failed to remove reminder";
            }
        } catch (NullPointerException e) {
            feedback = e.getMessage();
            success  = false;
        } catch (IllegalArgumentException e) {
            feedback = e.getMessage();
            success  = false;
        }

        eventHandler.setStatus(feedback);
        return success;
    }

    public boolean hasUndo() {
        return true;
    }

    public Event undo() {
        Event undo = new AddReminder();
        undo.setEventLisnter(eventHandler);

        undo.register(task, remindTime);
        
        boolean success = undo.execute();
        
        if (success) {
            return undo;
        } else {
            return null;
        }
    }

}
