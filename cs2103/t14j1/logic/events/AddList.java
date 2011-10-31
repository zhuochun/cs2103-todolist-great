package cs2103.t14j1.logic.events;

import cs2103.t14j1.storage.TaskList;

/**
 * a Add List event
 * 
 * @author Zhuochun
 *
 */
public class AddList extends Event {
    
    String listname;
    TaskList list;

    public void register(Object... objs) {
        if (objs[0] instanceof String) {
            listname = (String) objs[0];
            list = null;
        } else if (objs[0] instanceof TaskList) {
            list = (TaskList) objs[0];
        }
    }
    
    public void execute() {
        String feedback;
        boolean success;
        
        try {
            if (list == null) {
                list = new TaskList(listname);
            }
            
            success = eventHandler.getLists().addList(list);
            
            if (success) {
                eventHandler.displayNewList(listname);
                eventHandler.setModified();
                
                feedback = String.format(eventHandler.getMsg("msg.ADD_SUCCESS"), "LIST", listname);
            } else {
                feedback = String.format(eventHandler.getMsg("msg.LIST_EXIST"), listname);
            }
        } catch (NullPointerException e) {
            feedback = e.getMessage();
        }
        
        eventHandler.setStatus(feedback);
    }
    
    public boolean hasUndo() {
        return true;
    }
    
    public Event undo() {
        Event undo = new DeleteList();
        
        undo.register(listname);
        undo.execute();
        
        return undo;
    }
}
