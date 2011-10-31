package cs2103.t14j1.logic.events;

/**
 * a Add List event
 * 
 * @author Zhuochun
 *
 */
public class AddList extends Event {
    
    String listname;

    public void register(Object... objs) {
        listname = (String) objs[0];
    }
    
    public void execute() {
        String feedback;
        boolean success;
        
        try {
            success = eventHandler.getLists().addList(listname);
            
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
