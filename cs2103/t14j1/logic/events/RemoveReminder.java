package cs2103.t14j1.logic.events;

import java.util.Date;

import cs2103.t14j1.storage.Task;

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
        task = (Task) objs[0];
    }

    public void execute() {
        String feedback = null;

        try {
            remindTime = task.getReminder();

            boolean success = eventHandler.getReminder().removeReminder(task);

            if (success) {
                eventHandler.setModified();
                eventHandler.refreshTasks();

                feedback = String.format("Reminder for task \"%1$s\" is successfully removed", task.getName());
            } else {
                feedback = "Failed to remove reminder";
            }
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
        Event undo = new AddReminder();
        undo.setEventLisnter(eventHandler);

        undo.register(task, remindTime);
        undo.execute();

        return undo;
    }

}
