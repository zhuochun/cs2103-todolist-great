package cs2103.t14j1.taskmeter;

import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskList;
import cs2103.t14j1.storage.TaskLists;
import cs2103.t14j1.taskmeter.reminder.ReminderDialog;

/**
 * provide methods for logic to change GUI contents
 * 
 * @author Zhuochun
 *
 */
public interface EventListener {
    
    /**
     * get the resource string defined in taskmeter_res.properties
     * 
     * @param m
     * @return string
     */
    public String getMsg(String m);
    
    /**
     * get all the lists in application
     * 
     * @return tasklists
     */
    public TaskLists getLists();
    
    /**
     * get the indexed task currently displaying in GUI
     * 
     * @param index
     * @return Task
     */
    public Task getTask(int index);
    
    /**
     * get the reminder object for setting reminder
     * 
     * @return ReminderDialog
     */
    public ReminderDialog getReminder();
    
    /**
     * get the new list name user entered
     * 
     * @param oldName
     * @return String new name
     */
    public String getEditList(String oldName);
    
    /**
     * set the status bar in GUI
     * 
     * @param msg
     */
    public void setStatus(String msg);
    
    /**
     * notify GUI that contents have changed, remember to save data
     */
    public void setModified();
    
    /**
     * refresh the GUI to display the list
     * 
     * @param listname
     */
    public void displayList(String listname);
    
    /**
     * refresh the GUI lists and tasks
     */
    public void refreshAll();
    
    /**
     * refresh all lists display in GUI
     */
    public void refreshLists();
    
    /**
     * refresh all tasks display in GUI
     */
    public void refreshTasks();
    
    /**
     * set the searchResult, and display the search result in GUI
     * 
     * @param result
     */
    public void setSearch(TaskList result);
    
    /**
     * switch to the task (the last one in display) in list
     * 
     * @param list
     */
    public void switchToTask(String list);
    
    /**
     * switch to the list with name lsit
     * @param list
     */
    public void switchToList(String list);
    
    /**
     * edit task in display with index idx
     * 
     * @param idx
     */
    public void editIdxTask(int idx);
    
    /**
     * delete task in searchResult with index idx
     * 
     * @param idx
     */
    public void removeTaskInSearch(int idx);
}
