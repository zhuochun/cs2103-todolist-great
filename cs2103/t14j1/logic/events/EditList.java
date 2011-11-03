package cs2103.t14j1.logic.events;

import cs2103.t14j1.storage.TaskList;
import cs2103.t14j1.storage.TaskLists;

/**
 * a edit list event
 * 
 * @author Zhuochun
 * 
 */
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

    public boolean execute() {
        String feedback = null;
        boolean success = false;

        if (newName == null) {
            newName = eventHandler.getEditList(oldName);
        }

        try {
            success = renameList(oldName, newName);

            if (success) {
                eventHandler.setModified();
                eventHandler.refreshLists();

                feedback = String.format(eventHandler.getMsg("msg.RENAME_LIST"), oldName, newName);
            } else {
                feedback = String.format(eventHandler.getMsg("msg.RENAME_LIST_FAIL"), oldName);
            }
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

    private boolean renameList(String oldName, String newName) {
        TaskList list = eventHandler.getLists().getList(oldName);

        // If no list with the name oldListname exists
        if (list == null) {
            throw new NullPointerException(String.format(eventHandler.getMsg("msg.EDIT_NULL_LIST"), oldName));
        } else if (oldName.equals(TaskLists.INBOX) || oldName.equals(TaskLists.TRASH)) {
            throw new IllegalArgumentException(eventHandler.getMsg("msg.EDIT_DEFAULT_LIST"));
        } else if (eventHandler.getLists().hasList(newName)) {
            throw new IllegalArgumentException(String.format(eventHandler.getMsg("msg.LIST_EXIST"), newName));
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
        
        boolean success = undo.execute();
        
        if (success) {
            return undo;
        } else {
            return null;
        }
    }
}