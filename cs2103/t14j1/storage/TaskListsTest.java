package cs2103.t14j1.storage;

import java.util.Date;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs2103.t14j1.logic.DateFormat;

/**
 * this jUnit test class is used to test the iteration of TaskLists and TaskList
 * 
 * @author Zhuochun
 *
 */
public class TaskListsTest {
    
    TaskLists lists;
    
    @Before
    public void setUp() throws Exception {
        lists = new TaskLists();
        
        lists.addList("list1");
        lists.addList("list2");
        
        // add one task
        String name = "new task";
        String place = null;
        String list = "Inbox";
        Priority priority = Priority.IMPORTANT;
        Date startDateTime = null;
        Date endDateTime = null;
        Date deadline = null;
        Long duration = new Long(20);
        boolean status = Task.INCOMPLETE;
        
        Task newTask = new Task(name, place, list, priority, startDateTime, endDateTime, deadline, duration, status);
        lists.addTask(list, newTask);
        
        // add another task
        name = "new task 2";
        place = "i dont have a place";
        list = "Inbox";
        priority = Priority.NORMAL;
        startDateTime = DateFormat.strToDateLong("2011-9-20 14:20:20");
        endDateTime = DateFormat.strToDateLong("2011-9-20 15:20:30");
        deadline = DateFormat.strToDateLong("2011-10-6 20:20:20");
        status = Task.INCOMPLETE;
        
        newTask = new Task(name, place, list, priority, startDateTime, endDateTime, deadline, duration, status);
        lists.addTask(list, newTask);
        
        // add an empty task and set properties one by one
        newTask = new Task();
        lists.addTask("Inbox", newTask);
        
        newTask.setName("task 3");
        newTask.setPlace("hell");
        newTask.setPriority(Priority.LOW);
        newTask.setStartDateTime(null);
        newTask.setEndDateTime(null);
        newTask.setDeadline(DateFormat.strToDate("2011-10-10"));
        newTask.setDuration(new Long(20));
        newTask.setStatus(Task.COMPLETED);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testIterator() {
/*
Expected result is

*/
        for (Entry<String, TaskList> list : lists) {
            System.out.println(list.getKey());
            
            for (Task task : list.getValue()) {
                System.out.println("\t" + task.getName());
                System.out.println("\t" + task.getPriorityStr());
                System.out.println("\t" + task.getStatusStr());
                System.out.println("\t--------------");
            }
            
            System.out.println("--------------");
        }
    }
    
    @Test
    public void testMoveTask() {
        System.out.println(lists.moveTask("Inbox", "list1", 1));
        
        for (Entry<String, TaskList> tlist : lists) {
            System.out.println(tlist.getKey());
            
            for (Task task : tlist.getValue()) {
                System.out.println("\t" + task.getName());
                System.out.println("\t" + task.getPriorityStr());
                System.out.println("\t" + task.getStatusStr());
                System.out.println("\t--------------");
            }
            
            System.out.println("--------------");
        }
    }

}