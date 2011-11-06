package cs2103.t14j1.storage.user;

import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskLists;
import cs2103.t14j1.storage.When;

/**
 * create default tasks to help the user familiar with taskMeter
 * 
 * @auther Worapol
 */
public class Beginner {

    public static void createTasks(TaskLists lists) {
        // add welcome message task
        String   name     = "Welcome To TaskMeter";
        String   list     = TaskLists.INBOX;
        String   place    = "";
        Priority priority = Priority.IMPORTANT;
        When     when     = new When();
        Boolean  status   = Task.COMPLETED;
        
        Task newTask = new Task(name, place, list, priority, when, status);
        lists.addTask(list, newTask);
        
        status = Task.INCOMPLETE;
        
        // two most important hotkeys
        name = "You can press F6 will hide and open TaskMeter";
        when = new When();
        newTask = new Task(name, place, list, priority, when, status);
        lists.addTask(list, newTask);
        
        name = "You can press Ctrl + K to focus on SmartBar to start enter commands";
        when = new When();
        newTask = new Task(name, place, list, priority, when, status);
        lists.addTask(list, newTask);
        
        priority = Priority.NORMAL;
        
        // four most important commands
        name = "You can add a task using command: add";
        when = new When();
        newTask = new Task(name, place, list, priority, when, status);
        lists.addTask(list, newTask);
        
        name = "You can edit a task using command: edit";
        when = new When();
        newTask = new Task(name, place, list, priority, when, status);
        lists.addTask(list, newTask);
        
        name = "You can delete a task using command: delete";
        when = new When();
        newTask = new Task(name, place, list, priority, when, status);
        lists.addTask(list, newTask);
        
        name = "You can mark a task completed using command: done";
        when = new When();
        newTask = new Task(name, place, list, priority, when, status);
        lists.addTask(list, newTask);
        
        priority = Priority.LOW;
        
        // help for more tips
        name = "Find more about what TaskMeter can do for you, press F1 to read the help";
        when = new When();
        newTask = new Task(name, place, list, priority, when, status);
        lists.addTask(list, newTask);
    }
}