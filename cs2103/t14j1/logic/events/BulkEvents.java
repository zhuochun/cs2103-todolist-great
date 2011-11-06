package cs2103.t14j1.logic.events;

import java.util.ArrayList;

import cs2103.t14j1.logic.Commands;
import cs2103.t14j1.storage.Task;

/**
 * combine individual event into multiple events
 * 
 * @author Zhuochun
 *
 */
public class BulkEvents extends Event {
    
    Commands         command;
    Object[]         objArray;
    Object[]         parameters;
    ArrayList<Event> events;
    Event[]          undoEvents;

    /**
     * command, objs for each command
     */
    public void register(Object... objs) {
        // initial attributes
        events     = new ArrayList<Event>();
        parameters = new Object[2];
        
        // take in objs
        command    = (Commands) objs[0];
        
        if (command == Commands.BULK) {
            undoEvents = (Event[]) objs[1];
        } else {
            objArray   = (Object[]) objs[1];
            // take in additional parameters
            for (int i = 0; i < 2; i++) {
                if (objs.length > i + 2) {
                    parameters[i] = objs[i + 2];
                } else {
                    parameters[i] = null;
                }
            }
        }
    }

    public boolean execute() {
        if (command == Commands.BULK) {
            return executeUndoEvents();
        } else {
            return executeNewEvents();
        }
    }
    
    private boolean executeUndoEvents() {
        boolean success = false;
        
        boolean suppress = undoEvents.length > 1;
        int successCount = 0;
        int failedCount  = 0;
        
        // perform undo
        for (Event e : undoEvents) {
            Event undo = e.undo();
            
            if (undo != null) {
                events.add(undo);
                successCount++;
            } else {
                failedCount++;
            }
        }
        
        // determine success
        if (events.size() > 0) {
            success = true;
        } else {
            success = false;
        }
        
        // print result if necessary
        if (suppress) {
            StringBuilder feedback = new StringBuilder();
            feedback.append("Undo/Redo Bulk Events -> ");
            
            if (events.size() == 0) {
                feedback.append("failed");
            } else {
                feedback.append("succeed: ");
                feedback.append(successCount);
                
                if (failedCount > 0) {
                    feedback.append(", failed: ");
                    feedback.append(failedCount);
                }
            }
            
            eventHandler.setStatus(feedback.toString());
        }
        
        return success;
    }
    
    private boolean executeNewEvents() {
        boolean success  = false;
        
        boolean suppress = objArray.length > 1; // suppress output for individual event
        
        StringBuilder successIdx = new StringBuilder();
        StringBuilder failedIdx  = new StringBuilder();
        
        // perform event
        for (int i = objArray.length - 1; i >= 0; i--) { // backward to prevent delete problem
            Object obj  = objArray[i];
            Event event = Event.generateEvent(command);
            event.setEventLisnter(eventHandler);
            
            event.register(obj, parameters[0], parameters[1], suppress);
            boolean executed = event.execute();
            
            if (executed) {
                events.add(event);
                
                if (obj instanceof Integer) {
                    Integer objIdx = (Integer) obj;
                    successIdx.append(objIdx);
                } else if (obj instanceof Task) {
                    Task task = (Task) obj;
                    successIdx.append("\"" + task.getName() + "\"");
                }
                successIdx.append(", ");
            } else {
                if (obj instanceof Integer) {
                    Integer objIdx = (Integer) obj;
                    failedIdx.append(objIdx);
                } else if (obj instanceof Task) {
                    Task task = (Task) obj;
                    failedIdx.append("\"" + task.getName() + "\"");
                }
                failedIdx.append(", ");
            }
        }
        
        // determine success
        if (events.size() > 0) {
            success = true;
        } else {
            success = false;
        }
        
        // form feedback
        if (suppress) {
            StringBuilder feedback = new StringBuilder();
            feedback.append(command.toString().toLowerCase().replace('_', ' '));
            feedback.append(" -> ");
            
            if (events.size() == 0) {
                feedback.append("failed: ");
                feedback.append(failedIdx.toString().substring(0, failedIdx.length()-2));
            } else {
                feedback.append("succeed: ");
                feedback.append(successIdx.toString().substring(0, successIdx.length()-2));

                if (failedIdx.length() > 0) {
                    feedback.append("; ");
                    feedback.append("failed: ");
                    feedback.append(failedIdx.toString().substring(0, failedIdx.length()-2));
                }

            }

            eventHandler.setStatus(feedback.toString());
        }

        return success;
    }

    public boolean hasUndo() {
        if (events.isEmpty()) {
            return false;
        }
        
        return events.get(0).hasUndo();
    }

    public Event undo() {
        Event undo = new BulkEvents();
        undo.setEventLisnter(eventHandler);
        
        // convert events to array
        Event[] undoEvents = events.toArray(new Event[events.size()]);

        undo.register(Commands.BULK, undoEvents);
        boolean success = undo.execute();
        
        if (success) {
            return undo;
        } else {
            return null;
        }
    }
}