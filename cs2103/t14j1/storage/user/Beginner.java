package cs2103.t14j1.storage.user;

import cs2103.t14j1.logic.DateFormat;
import cs2103.t14j1.storage.Priority;
import cs2103.t14j1.storage.Task;
import cs2103.t14j1.storage.TaskLists;
import cs2103.t14j1.storage.When;

/**
 * create default tasks to help the user familiar with taskMeter
 */
public class Beginner {

    public static void createTasks(TaskLists lists) {
        // add welcome message task
        String name  = "Welcome To TaskMeter";
        String list  = TaskLists.INBOX;
        String place = "";
        
        Priority priority = Priority.IMPORTANT;
        
        When when = new When();
        //when.setDeadline(DateFormat.getNow());
        
        Boolean status = Task.INCOMPLETE;
        
        Task newTask = new Task(name, place, list, priority, when, status);
        lists.addTask(list, newTask);
        
        // add how to add task - hotkey
        name = "Press Ctrl + N to Add a New Task";
        list = TaskLists.INBOX;
        
        newTask = new Task(name, place, list, priority, when, status);
        lists.addTask(list, newTask);
        
        // add how to add task - smartBar
        name = "Or Enter \"add\" to Add a New Task";
        list = TaskLists.INBOX;
        
        newTask = new Task(name, place, list, priority, when, status);
        lists.addTask(list, newTask);
        
        // add how to edit task
        name  = "Press Ctrl + E to Edit a Task";
        list  = TaskLists.INBOX;
        place = "";
        
        newTask = new Task(name, place, list, priority, when, status);
        lists.addTask(list, newTask);
        
        // add how to use smartBar
        name  = "Press F1 for more tips";
        list  = TaskLists.INBOX;
        place = "";
        
        newTask = new Task(name, place, list, priority, when, status);
        lists.addTask(list, newTask);
    }
}
