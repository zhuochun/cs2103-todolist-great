package cs2103.t14j1.logic.events;

import cs2103.t14j1.storage.TaskList;
import cs2103.t14j1.storage.TaskLists;

/**
 * a delete list event
 * 
 * @author Zhuochun
 * 
 */
public class DeleteList extends Event {

    String name;
    TaskList list;

    public void register(Object... objs) {
        name = (String) objs[0];
    }

    public boolean execute() {
        String feedback = null;
        boolean success = false;

        try {
            list = eventHandler.getLists().getList(name);
            
            success = deleteList(name);

            if (success) {
                eventHandler.setModified();
                eventHandler.refreshAll();

                feedback = String.format(eventHandler.getMsg("msg.DELETE_SUCCESS"), "List", name);
            } else {
                feedback = String.format(eventHandler.getMsg("msg.DELETE_FAIL"), "List");
            }
        } catch (Exception e) {
            feedback = e.getMessage();
            success  = false;
        }

        eventHandler.setStatus(feedback);
        return success;
    }

    private boolean deleteList(String listName) {
        if (listName.equals(TaskLists.INBOX) || listName.equals(TaskLists.TRASH))
            return false;

        if (!eventHandler.getLists().hasList(listName))
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
        
        boolean success = undo.execute();
        
        if (success) {
            return undo;
        } else {
            return null;
        }
    }

}
