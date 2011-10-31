package cs2103.t14j1.logic.events;

import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskLists;

public class MoveTask extends Event {
    
    Task task;
    String oldListName;
    String newListName;
    
    public void register(Object... objs) {
        int index   = (Integer) objs[0];
        newListName = (String) objs[2];
        task        = eventHandler.getTask(index);
        oldListName = task.getList();
    }
    
    public void execute() {
        String feedback = null;
        
        try {
            TaskLists lists = eventHandler.getLists();
            
            lists.moveTask(newListName, task);
            
            eventHandler.setModified();
            eventHandler.refreshDisplay();
            
            feedback = String.format(eventHandler.getMsg("msg.MOVE"), task.getName(), newListName);
        } catch (IllegalArgumentException e) {
            feedback = e.getMessage();
        } catch (NullPointerException e) {
            feedback = e.getMessage();
        } catch (IndexOutOfBoundsException e) {
            feedback = e.getMessage();
        }
        
        eventHandler.setStatus(feedback);
    }
    
    public boolean hasUndo() {
        return true;
    }
    
    public Event undo() {
        return null;
    }

}
