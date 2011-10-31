package cs2103.t14j1.logic.events;

import java.util.Date;

import cs2103.t14j1.logic.DateFormat;
import cs2103.t14j1.storage.Task;

/**
 * a add reminder event
 * 
 * @author Zhuochun
 * 
 */
public class AddReminder extends Event {

    Task task;
    Date remindTime;

    public void register(Object... objs) {
        task = (Task) objs[0];
        remindTime = (Date) objs[1];
    }

    public boolean execute() {
        String feedback;
        boolean success = false;

        try {
            eventHandler.getReminder().addReminder(remindTime, task);

            eventHandler.refreshTasks();
            eventHandler.setModified();

            success  = true;
            feedback = String.format(eventHandler.getMsg("msg.ADD_REMINDER"), task.getName(),
                    DateFormat.dateToStrShort(remindTime));
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
        Event undo = new RemoveReminder();
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
