package cs2103.t14j1.storage;

import java.util.Date;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cs2103.t14j1.logic.DateFormat;

/**
 * this jUnit class tests the fileHandler on load (save) lists and tasks
 * 
 * @author Zhuochun
 * 
 */
public class FileHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSaveAll() {
        TaskLists lists = new TaskLists();

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
        
        FileHandler.saveAll(lists);
    }

    @Test
    public void testLoadAll() {
        TaskLists newLists = new TaskLists();
        
        FileHandler.loadAll(newLists);
        
        for (Entry<String, TaskList> list : newLists) {
            System.out.println(list.getKey());
            
            for (Task task : list.getValue()) {
                System.out.println("\t" + task.getName());
                System.out.println("\t" + task.getList());
                System.out.println("\t" + task.getStartLong());
                System.out.println("\t" + task.getEndLong());
                System.out.println("\t" + task.getStatusStr());
                System.out.println("\t=====");
            }
            
            System.out.println("--------------");
        }
    }

}