package cs2103.t14j1.logic.events;

import cs2103.t14j1.storage.TaskList;

/**
 * a search Event
 * 
 * @author Zhuochun
 * 
 */
public class Search extends Event {

    TaskList searchResult;

    public void register(Object... objs) {
        searchResult = (TaskList) objs[0];
    }

    public boolean execute() {
        eventHandler.setSearch(searchResult);
        return true;
    }

    public boolean hasUndo() {
        return false;
    }

    public Event undo() {
        Event undo = new Search();
        undo.setEventLisnter(eventHandler);

        undo.register(searchResult);
        undo.execute();

        return undo;
    }

}
