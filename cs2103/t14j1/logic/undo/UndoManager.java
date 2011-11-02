package cs2103.t14j1.logic.undo;

import java.util.ArrayList;

import cs2103.t14j1.logic.events.Event;

/**
 * A undo and redo Manager
 * 
 * @author Zhuochun
 * 
 */
public class UndoManager {

    private ArrayList<Event> undoList;
    private ArrayList<Event> redoList;

    private int MAX_STORAGE = 50;

    public UndoManager() {
        undoList = new ArrayList<Event>();
        redoList = new ArrayList<Event>();
    }

    public boolean hasUndo() {
        return !undoList.isEmpty();
    }

    public boolean hasRedo() {
        return !redoList.isEmpty();
    }

    public Event getUndo() {
        if (undoList.isEmpty()) {
            return null;
        }

        return undoList.remove(undoList.size() - 1);
    }

    public Event getRedo() {
        if (redoList.isEmpty()) {
            return null;
        }

        return redoList.remove(redoList.size() - 1);
    }

    public void addUndo(Event e) {
        if (undoList.size() >= MAX_STORAGE) {
            undoList.remove(0);
        }

        undoList.add(e);
    }

    public void addRedo(Event e) {
        if (redoList.size() >= MAX_STORAGE) {
            redoList.remove(0);
        }

        redoList.add(e);
    }

    public void setMaxStorage(int max) {
        MAX_STORAGE = max;
    }
}
