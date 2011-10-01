package cs2103.t14j1.storage;

import static org.junit.Assert.*;

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
        
        lists.add("list1");
        lists.add("list2");
        
        String name = "new task";
        String list = "Inbox";
        Priority priority = Priority.IMPORTANT;
        Date startDateTime = null;
        Date endDateTime = null;
        boolean status = Task.NOT_COMPLETED;
        Task newTask = new Task(name, list, priority, startDateTime, endDateTime, status);
        lists.addTask(list, newTask);
        
        // add another task
        name = "new task 2";
        list = "Inbox";
        priority = Priority.NORMAL;
        startDateTime = DateFormat.strToDateLong("2011-9-20 14:20:20");
        endDateTime = DateFormat.strToDateLong("2011-9-20 15:20:30");
        status = Task.NOT_COMPLETED;
        newTask = new Task(name, list, priority, startDateTime, endDateTime, status);
        lists.addTask(list, newTask);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testIterator() {
/*
 * Expected result is
 * 
 * Inbox
 *  new task
 *  new task 2
 * --------------
 * Trash
 * --------------
 * list1
 * --------------
 * list2
 * --------------
 * 
 */
        for (Entry<String, TaskList> list : lists) {
            System.out.println(list.getKey());
            
            for (Task task : list.getValue()) {
                System.out.println("\t" + task.getName());
            }
            
            System.out.println("--------------");
        }
    }

}
