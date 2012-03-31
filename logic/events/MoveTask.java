package logic.events;

import storage.Task;
import storage.TaskLists;

/**
 * a move task event
 * 
 * @author Zhuochun
 * 
 */
public class MoveTask extends Event {

    int index;
    Task task;
    String oldListName;
    String newListName;
    boolean newListAdded;

    public void register(Object... objs) {
        if (objs[0] instanceof Integer) {
            index = (Integer) objs[0];
            task  = null;
        } else if (objs[0] instanceof Task) {
            task  = (Task) objs[0];
            index = -1;
        }

        newListName  = (String) objs[1];
        newListAdded = false;
    }

    public boolean execute() {
        String feedback = null;
        boolean success = false;

        try {
            if (task == null) {
                task = eventHandler.getTask(index);
            }
            
            oldListName = task.getList();

            TaskLists lists = eventHandler.getLists();
            
            if (!lists.hasList(newListName)) {
                newListAdded = true;
            }

            lists.moveTask(newListName, task);

            eventHandler.setModified();
            
            if (!newListName.equals(TaskLists.TRASH)) {
                eventHandler.switchToTask(task.getList());
            }
            
            eventHandler.refreshAll();

            success  = true;
            feedback = String.format(eventHandler.getMsg("msg.MOVE"), task.getName(), newListName);
        } catch (IllegalArgumentException e) {
            success  = false;
            feedback = e.getMessage();
        } catch (NullPointerException e) {
            success  = false;
            feedback = e.getMessage();
        } catch (IndexOutOfBoundsException e) {
            success  = false;
            feedback = e.getMessage();
        }

        eventHandler.setStatus(feedback);
        return success;
    }

    public boolean hasUndo() {
        return true;
    }

    public Event undo() {
        Event undo = new MoveTask();
        undo.setEventLisnter(eventHandler);

        undo.register(task, oldListName);
        
        boolean success = undo.execute();
        
        if (success) {
            // if a new list is added, delete it
            if (newListAdded) {
                Event del = new DeleteList();
                del.setEventLisnter(eventHandler);
                del.register(newListName);
                del.execute();
            }
        
            return undo;
        } else {
            return null;
        }
    }

}
