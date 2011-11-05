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
            events = (ArrayList<Event>) objs[1];
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
            return false;
        } else {
            return executeNewEvents();
        }
    }
    
    private boolean executeNewEvents() {
        String  feedback;
        boolean success  = false;
        
        boolean suppress = true; // suppress output for individual event
        
        if (objArray.length == 1) {
            suppress = false;
        }
        
        StringBuilder successIdx = new StringBuilder();
        StringBuilder failedIdx  = new StringBuilder();
        
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
                    successIdx.append("\"" + task.getName().substring(0, 9) + "..\"");
                }
                successIdx.append(", ");
            } else {
                if (obj instanceof Integer) {
                    Integer objIdx = (Integer) obj;
                    failedIdx.append(objIdx);
                } else if (obj instanceof Task) {
                    Task task = (Task) obj;
                    failedIdx.append("\"" + task.getName().substring(0, 9) + "..\"");
                }
                failedIdx.append(", ");
            }
        }
        
        // form feedback
        if (suppress) {
            StringBuilder fb = new StringBuilder();
            fb.append(command.toString().toLowerCase().replace('_', ' '));
            fb.append(" -> ");
            
            if (events.size() == 0) {
                success  = false;

                fb.append("failed: ");
                fb.append(failedIdx.toString().substring(0, failedIdx.length()-2));
            } else {
                success  = true;

                fb.append("succeed: ");
                fb.append(successIdx.toString().substring(0, successIdx.length()-2));

                if (failedIdx.length() > 0) {
                    fb.append("; ");
                    fb.append("failed: ");
                    fb.append(failedIdx.toString().substring(0, failedIdx.length()-2));
                }

            }

            feedback = fb.toString();

            eventHandler.setStatus(feedback);
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

        undo.register(Commands.BULK, events);
        boolean success = undo.execute();
        
        if (success) {
            return undo;
        } else {
            return null;
        }
    }
}