package cs2103.t14j1.logic.events;

public class AddList extends Event {

    public void register(Object... objs) {
    }
    
    public void execute() {
    }
    
    public boolean hasUndo() {
        return false;
    }
    
    public Event undo() {
        return null;
    }
}
