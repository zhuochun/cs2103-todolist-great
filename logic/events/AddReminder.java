package logic.events;

import gui.reminder.Reminder;

import java.util.Date;

import storage.Task;

import logic.DateFormat;


/**
 * a add reminder event
 * 
 * @author Zhuochun
 * 
 */
public class AddReminder extends Event {

    Task task;
    Date remindTime;

    /**
     * task, Remind type, Date (if type is CUSTOM)
     */
    public void register(Object... objs) {
        task = (Task) objs[0];
        
        Reminder parameter = (Reminder) objs[1];
        
        switch (parameter) {
            case START:
                remindTime = task.getStartDateTime();
                break;
            case END:
                remindTime = task.getEndDateTime();
                break;
            case DEADLINE:
                remindTime = task.getDeadline();
                break;
            case CUSTOM:
                remindTime = (Date) objs[2];
                break;
        }
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
        } catch (IndexOutOfBoundsException e) {
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
