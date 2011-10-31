package cs2103.t14j1.logic.events;

import cs2103.t14j1.storage.TaskList;

public class DeleteList extends Event {
    
    String   name;
    TaskList list;
    
    public void register(Object... objs) {
        name = (String) objs[0];
        list = eventHandler.getLists().getList(name);
    }
    
    public void execute() {
        String feedback = null;
        
        try {
            boolean success = deleteList(name);
            
            if (success) {
                eventHandler.setModified();
                eventHandler.refreshDisplay();
                
                feedback = String.format(eventHandler.getMsg("msg.DELETE_SUCCESS"), "LIST", name);
            } else {
                feedback = String.format(eventHandler.getMsg("msg.DELETE_FAIL"), "LIST");
            }
        } catch (Exception e) {
            feedback = e.getMessage();
        }
        
        eventHandler.setStatus(feedback);
    }
    
    private boolean deleteList(String listName) {
    	if(listName.equals("Inbox") || listName.equals("Trash"))
    		return false;
    	
    	if(!eventHandler.getLists().hasList(listName))
    		return false;
    	
    	eventHandler.getLists().removeList(listName);
    	
    	return true;
    }
    
    public boolean hasUndo() {
        return true;
    }
    
    public Event undo() {
        Event undo = new AddList();
        undo.setEventLisnter(eventHandler);
        
        undo.register(list);
        undo.execute();
        
        return undo;
    }

}
