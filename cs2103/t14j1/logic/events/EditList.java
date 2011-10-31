package cs2103.t14j1.logic.events;

import cs2103.t14j1.storage.TaskList;

public class EditList extends Event {
    
    String newName;
    String oldName;
    
    /**
     * oldListName, newListName
     */
    public void register(Object... objs) {
        oldName = (String) objs[0];
        newName = (String) objs[1];
    }
    
    public void execute() {
        String feedback = null;
        
        if (newName == null) {
            newName = eventHandler.getEditList(oldName);
        }
        
        try {
            boolean success = renameList(oldName, newName);
            
            if (success) {
                eventHandler.setModified();
                eventHandler.refreshLists();
                
                feedback = String.format(eventHandler.getMsg("msg.RENAME_LIST"), oldName, newName);
            } else {
                feedback = String.format(eventHandler.getMsg("msg.RENAME_LIST_FAIL"), oldName);
            }
        } catch (NullPointerException e) {
            feedback = e.getMessage();
        }
        
        eventHandler.setStatus(feedback);
    }
    
    private boolean renameList(String oldName, String newName) {
    	TaskList list = eventHandler.getLists().getList(oldName);
    	
    	//If no list with the name oldListname exists
    	if (list == null) {
    		throw new NullPointerException(String.format(eventHandler.getMsg("msg.EDIT_NULL_LIST"), oldName));
    	}
    	
    	list.setName(newName);
    	
    	// remove oldList from lists because lists use treeMap
    	eventHandler.getLists().removeList(oldName);
    	eventHandler.getLists().addList(list);
    	
    	return true;
    }
    
    public boolean hasUndo() {
        return true;
    }
    
    public Event undo() {
        Event undo = new EditList();
        undo.setEventLisnter(eventHandler);
        
        undo.register(newName, oldName);
        undo.execute();
        
        return undo;
    }
}