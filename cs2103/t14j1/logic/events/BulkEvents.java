package cs2103.t14j1.logic.events;

import java.util.ArrayList;

import cs2103.t14j1.logic.Commands;

/**
 * combine individual event into multiple events
 * 
 * @author Zhuochun
 *
 */
public class BulkEvents extends Event {
    
    Commands         command;
    Integer[]        ids;
    Object           parameter;
    ArrayList<Event> events;

    /**
     * command, objs for each command
     */
    public void register(Object... objs) {
        events    = new ArrayList<Event>();
        
        command   = (Commands)  objs[0];
        ids       = (Integer[]) objs[1];
        
        if (objs.length > 2) {
            parameter = objs[2];
        } else {
            parameter = null;
        }
    }

    public boolean execute() {
        String  feedback;
        boolean success  = false;
        
        boolean suppress = true; // suppress output for individual event
        
        if (ids.length == 1) {
            suppress = false;
        }
        
        StringBuilder successIdx = new StringBuilder();
        StringBuilder failedIdx  = new StringBuilder();
        
        for (int i : ids) {
            Event event = Event.generateEvent(command);
            event.setEventLisnter(eventHandler);
            
            if (parameter != null) {
                event.register(i, parameter, suppress);
            } else {
                event.register(i, suppress);
            }
            
            boolean executed = event.execute();
            
            if (executed) {
                events.add(event);
                
                successIdx.append(i);
                successIdx.append(", ");
            } else {
                failedIdx.append(i);
                failedIdx.append(", ");
            }
        }
        
        // form feedback
        if (suppress) {
            StringBuilder fb = new StringBuilder();

            if (events.size() == 0) {
                success  = false;

                fb.append("Failed: ");
                fb.append(failedIdx.toString().substring(0, failedIdx.length()-2));
            } else {
                success  = true;

                fb.append("Succeed: ");
                fb.append(successIdx.toString().substring(0, successIdx.length()-2));

                if (failedIdx.length() > 0) {
                    fb.append("; ");
                    fb.append("Failed: ");
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

        undo.register(command, events);
        
        boolean success = undo.execute();
        
        if (success) {
            return undo;
        } else {
            return null;
        }
    }

}