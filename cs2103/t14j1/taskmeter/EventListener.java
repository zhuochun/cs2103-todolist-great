package cs2103.t14j1.taskmeter;

import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskList;
import cs2103.t14j1.storage.TaskLists;
import cs2103.t14j1.taskmeter.reminder.ReminderDialog;

public interface EventListener {
    
    public String getMsg(String m);
    
    public TaskLists getLists();
    
    public Task getTask(int index);
    
    public ReminderDialog getReminder();
    
    public String getEditList(String oldName);
    
    public void setStatus(String msg);
    
    public void setModified();
    
    public void displayNewList(String listname);
    
    public void refreshDisplay();
    
    public void refreshLists();
    
    public void refreshTasks();
    
    public void setSearch(TaskList result);
    
    public void switchTask(String list);
    
    public void switchList(String list);
    
    public void editTask(int idx);
}