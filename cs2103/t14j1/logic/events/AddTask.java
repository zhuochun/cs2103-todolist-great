package cs2103.t14j1.logic.events;

import cs2103.t14j1.storage.Task;

public class AddTask extends Event {
    
    Task task;
    
    public void register(Object... objs) {
        task = (Task) objs[0];
    }
    
    public void execute() {
        String feedback;
        boolean success;
        
        success = eventHandler.getLists().addTask(task.getList(), task);
        
        if (success) {
            feedback = String.format(eventHandler.getMsg("msg.ADD_TASK_SUCCESS"), task.getName(), task.getList());
            
            eventHandler.setModified();

            if (mode == MODE_LIST && newTask.getList().equals(currentList.getName())) {
                displayNewTask(taskTable.getItemCount()+1, newTask);
            }
        } else {
            feedback = String.format(eventHandler.getMsg("msg.ADD_FAIL"), "TASK");
        }

        eventHandler.refreshLists();
        eventHandler.switchTask(task.getList());
        eventHandler.setStatus(feedback);
    }
    
    public boolean hasUndo() {
        return true;
    }
    
    public Event undo() {
        return null;
    }

}
